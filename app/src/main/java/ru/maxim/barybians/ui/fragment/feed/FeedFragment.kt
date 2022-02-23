package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.databinding.FragmentPostEditorBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.feed.FeedViewModel.FeedViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class FeedFragment : MvpAppCompatFragment(R.layout.fragment_feed), FeedAdapterListener {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var viewModelFactory: FeedViewModelFactory
    private val model: FeedViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentFeedBinding::bind)

    @Inject
    lateinit var feedRecyclerAdapter: FeedRecyclerAdapter

    @Inject
    lateinit var loadingStateAdapter: LoadingStateAdapter

    private var currentLoadingState: LoadState? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        feedRefreshLayout.setOnRefreshListener(feedRecyclerAdapter::refresh)

        loadingStateAdapter.setOnRetryListener(feedRecyclerAdapter::retry)
        feedRecyclerAdapter.setAdapterListener(this@FeedFragment)
        feedRecyclerView.adapter = feedRecyclerAdapter.withLoadStateFooter(loadingStateAdapter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                feedRecyclerAdapter.loadStateFlow
                    .debounce(100)
                    .mapNotNull { loadState -> loadState.mediator?.refresh }
                    .collectLatest { loadState ->
                        if (loadState == currentLoadingState) return@collectLatest
                        currentLoadingState = loadState

                        feedProgressBar.isVisible = loadState is Loading && !feedRefreshLayout.isRefreshing
                        feedRefreshLayout.isRefreshing = feedRefreshLayout.isRefreshing && loadState is Loading
                        feedMessage.isVisible = loadState is Error && model.postsCount == 0
                        if (loadState is Error) {
                            var errorMessage = when (loadState.error) {
                                is NoConnectionException -> getString(R.string.no_internet_connection)
                                is TimeoutException -> getString(R.string.request_timeout)
                                else -> getString(R.string.an_error_occurred_while_loading_feed)
                            }
                            if (preferencesManager.isDebug) {
                                errorMessage += "\n${loadState.error}(${loadState.error.message})"
                            }
                            feedMessage.text = errorMessage
                            if (model.postsCount > 0) {
                                context?.toast(errorMessage)
                            }
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.feed.collect { posts ->
                    feedRefreshLayout.isRefreshing = false
                    feedRecyclerAdapter.submitData(posts)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { messageRes -> context?.toast(messageRes) }
        }
    }

    override fun onProfileClick(userId: Int) {
        findNavController().navigate(FeedFragmentDirections.toProfile(userId))
    }

    override fun onImageClick(imageUrl: String) {
        findNavController().navigate(FeedFragmentDirections.toImageViewer(imageUrl = imageUrl))
    }

    override fun onPostMenuClick(post: Post, anchor: View) {
        val activity = activity ?: return
        PopupMenu(activity, anchor).apply {
            activity.menuInflater.inflate(R.menu.menu_post, menu)

            menu.findItem(R.id.menuPostEdit)?.setOnMenuItemClickListener {
                showEditDialog(post.title, post.text) { title, text ->
                    model.editPost(postId = post.postId, title = title, text = text)
                }
                true
            }

            menu.findItem(R.id.menuPostDelete)?.setOnMenuItemClickListener {
                showDeleteDialog { model.deletePost(postId = post.postId) }
                true
            }
            show()
        }
    }

    private fun showDeleteDialog(onDelete: () -> Unit) {
        MaterialAlertDialogBuilder(context ?: return).apply {
            setTitle(R.string.delete_this_post)
            setMessage(R.string.this_action_cannot_be_undone)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.ok) { _, _ -> onDelete() }
        }.show()
    }

    private fun showEditDialog(title: String?, text: String, onEdit: (title: String?, text: String) -> Unit) {
        val editPostDialogBinding = FragmentPostEditorBinding.inflate(layoutInflater).apply {
            fragmentPostEditorTitle.setText(title)
            fragmentPostEditorText.setText(text)
            fragmentPostEditorTitle.addTextChangedListener { fragmentPostEditorTitleLayout.error = null }
            fragmentPostEditorText.addTextChangedListener { fragmentPostEditorTextLayout.error = null }
        }
        val editPostDialog = MaterialAlertDialogBuilder(context ?: return).apply {
            setTitle(R.string.edit_post)
            setView(editPostDialogBinding.root)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.ok, null)
        }.create()

        editPostDialog.setOnShowListener { dialog ->
            (dialog as? AlertDialog)?.getButton(BUTTON_POSITIVE)?.setOnClickListener {
                val newTitle = editPostDialogBinding.fragmentPostEditorTitle.text.toString()
                val newText = editPostDialogBinding.fragmentPostEditorText.text.toString()
                if (newText.isBlank()) {
                    editPostDialogBinding.fragmentPostEditorTextLayout.error = getString(R.string.this_field_is_required)
                } else if (title == newTitle && text == newText) {
                    editPostDialogBinding.fragmentPostEditorTextLayout.error = getString(R.string.there_is_no_changes)
                    editPostDialogBinding.fragmentPostEditorTitleLayout.error = getString(R.string.there_is_no_changes)
                } else {
                    onEdit(newTitle, newText)
                    dialog.dismiss()
                }
            }
        }

        editPostDialog.show()
    }

    override fun onCommentsClick(postId: Int) {
        findNavController().navigate(FeedFragmentDirections.toCommentsList(postId))
    }

    override fun onLikeClick(postId: Int) {
        model.changeLike(postId)
    }

    override fun onLikeLongClick(postId: Int) {
        findNavController().navigate(FeedFragmentDirections.toLikesList(postId))
    }

    override fun onDestroyView() {
        feedRecyclerAdapter.setAdapterListener(null)
        super.onDestroyView()
    }
}