package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.feed.FeedViewModel.FeedViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.size
import ru.maxim.barybians.utils.toast
import timber.log.Timber
import javax.inject.Inject

class FeedFragment : MvpAppCompatFragment(R.layout.fragment_feed), FeedAdapterListener {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        feedRefreshLayout.setOnRefreshListener(feedRecyclerAdapter::refresh)

        loadingStateAdapter.setOnRetryListener(feedRecyclerAdapter::retry)
        feedRecyclerAdapter.setAdapterListener(this@FeedFragment)
        feedRecyclerView.adapter = feedRecyclerAdapter.withLoadStateFooter(loadingStateAdapter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                feedRecyclerAdapter.loadStateFlow.collectLatest { loadState ->
                    val state = loadState.mediator?.refresh ?: return@collectLatest
                    delay(20)
                    if (state == currentLoadingState) return@collectLatest
                    currentLoadingState = state
                    Timber.d("XXX state: $state itemCount: ${feedRecyclerAdapter.itemCount} size: ${model.feed.value.size()}")

                    feedProgressBar.isVisible = state is Loading && !feedRefreshLayout.isRefreshing
                    feedRefreshLayout.isRefreshing = feedRefreshLayout.isRefreshing && state is Loading
                    feedMessage.isVisible = state is Error && feedRecyclerAdapter.itemCount == 0
                    if (state is Error) {
                        val errorMessage = when (state.error) {
                            is NoConnectionException -> getString(R.string.no_internet_connection)
                            is TimeoutException -> getString(R.string.request_timeout)
                            else -> getString(R.string.an_error_occurred_while_loading_feed)
                        }
                        feedMessage.text = errorMessage
                        if (feedRecyclerAdapter.itemCount > 0) {
                            context?.toast(errorMessage)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(STARTED) {
                model.feed.collect { posts ->
                    feedRecyclerAdapter.submitData(posts)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.messageRes.observe(viewLifecycleOwner) { messageRes -> context?.toast(messageRes) }
        }
    }

    override fun onProfileClick(userId: Int) {
        findNavController().navigate(FeedFragmentDirections.toProfile(userId))
    }

    override fun onImageClick(imageUrl: String) {
        findNavController().navigate(FeedFragmentDirections.toImageViewer(imageUrl = imageUrl))
    }

    override fun onPostMenuClick(post: Post) {
//        PostMenuDialog.newInstance(
//            title = post.title,
//            text = post.text,
//            onDelete = {
//                model.deletePost(post.postId)
//            },
//            onEdit = { title, text ->
//                model.editPost(post.postId, title, text)
//            }
//        ).show(childFragmentManager, PostMenuDialog::class.simpleName)
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