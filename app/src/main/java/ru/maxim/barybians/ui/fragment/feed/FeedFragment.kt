package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.dialog.editPost.EditPostDialog
import ru.maxim.barybians.ui.fragment.feed.FeedViewModel.FeedViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.longToast
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

open class FeedFragment : Fragment(R.layout.fragment_feed), FeedAdapterListener {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var feedViewModelFactory: FeedViewModelFactory
    protected open val model: FeedViewModel by viewModels { feedViewModelFactory }

    protected val binding by viewBinding(FragmentFeedBinding::bind)

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
        isCreateButtonShown = true
        feedRefreshLayout.setOnRefreshListener(::refresh)
        feedCreatePostButton.setOnClickListener { showEditDialog(R.string.new_post, onEdit = model::createPost) }

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                feedRecyclerAdapter.loadStateFlow
                    .mapNotNull { loadState -> loadState.mediator?.refresh }
                    .collectLatest { loadState ->
                        if (loadState == currentLoadingState) return@collectLatest
                        currentLoadingState = loadState

                        feedRefreshLayout.isRefreshing = loadState is Loading
                        if (loadState is Error) {
                            var errorMessage = when (loadState.error) {
                                is NoConnectionException -> getString(R.string.no_internet_connection)
                                is TimeoutException -> getString(R.string.request_timeout)
                                else -> getString(R.string.an_error_occurred_while_loading_feed)
                            }
                            if (preferencesManager.isDebug) {
                                errorMessage += "\n${loadState.error}(${loadState.error.message})"
                                context?.longToast(errorMessage)
                            } else {
                                context?.toast(errorMessage)
                            }
                            feedMessage.text = errorMessage
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.postsList.collect { posts ->
                    feedRecyclerAdapter.submitData(posts)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.postsCount.collect { postsCount -> feedMessage.isVisible = postsCount == 0 }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessage.observe(viewLifecycleOwner) { messageRes -> context?.toast(messageRes) }
        }
    }

    open fun refresh() {
        feedRecyclerAdapter.refresh()
    }

    open fun setupRecyclerView() {
        loadingStateAdapter.setOnRetryListener(feedRecyclerAdapter::retry)
        feedRecyclerAdapter.setAdapterListener(this@FeedFragment)
        binding.feedRecyclerView.adapter = feedRecyclerAdapter.withLoadStateFooter(loadingStateAdapter)
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
                showEditDialog(R.string.edit_post, post.title, post.text) { title, text ->
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

    private fun showEditDialog(
        @StringRes dialogTitle: Int,
        title: String? = null,
        text: String = String(),
        onEdit: (title: String?, text: String) -> Unit
    ) {
        EditPostDialog(
            context = context ?: return,
            dialogTitle = dialogTitle,
            title = title,
            text = text,
            onPositiveButtonClicked = onEdit
        ).show()
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