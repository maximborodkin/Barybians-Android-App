package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.ui.fragment.feed.FeedViewModel.FeedViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.hide
import ru.maxim.barybians.utils.show
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class FeedFragment : MvpAppCompatFragment(R.layout.fragment_feed), FeedAdapterListener {

    @Inject
    lateinit var viewModelFactory: FeedViewModelFactory
    private val model: FeedViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentFeedBinding::bind)

    @Inject
    lateinit var feedRecyclerAdapter: FeedRecyclerAdapter

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        feedRefreshLayout.setOnRefreshListener(feedRecyclerAdapter::refresh)
        feedRecyclerView.adapter = feedRecyclerAdapter
        feedRecyclerAdapter.setFeedItemsListener(this@FeedFragment)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                feedRecyclerAdapter.loadStateFlow.collect { state ->
                    when (state.refresh) {
                        is LoadState.Loading -> {
                            feedProgressBar.isVisible = feedRecyclerAdapter.itemCount == 0
                            feedMessage.hide()
                        }
                        is LoadState.NotLoading -> {
                            feedProgressBar.hide()
                            if (feedRecyclerAdapter.itemCount > 0) feedMessage.hide()
                            feedRefreshLayout.isRefreshing = false
                        }
                        is LoadState.Error -> {
                            feedProgressBar.hide()
                            feedRefreshLayout.isRefreshing = false
                            val errorMessage = getString(R.string.an_error_occurred_while_loading_feed)
                            if (feedRecyclerAdapter.itemCount == 0) {
                                feedMessage.show()
                                feedMessage.text = errorMessage
                            } else {
                                context?.toast(errorMessage)
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.feed.collect(feedRecyclerAdapter::submitData)
            }
        }
    }

    override fun onProfileClick(userId: Int) {
        findNavController().navigate(FeedFragmentDirections.toProfile(userId))
    }

    override fun onImageClick(imageUrl: String) {
        findNavController().navigate(FeedFragmentDirections.toImageViewer(imageUrl = imageUrl))
    }

    override fun onPostMenuClick(postId: Int) {
//        commentsRecyclerAdapter.currentList.find { it.id == postId }?.let { post ->
//            PostMenuDialog.newInstance(
//                title = post.title,
//                text = post.text,
//                onDelete = {
//                    model.deletePost(post.id)
//                },
//                onEdit = { title, text ->
//                    model.editPost(postId, title, text)
//                }
//            ).show(childFragmentManager, PostMenuDialog::class.simpleName)
//        }
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
        feedRecyclerAdapter.setFeedItemsListener(null)
        super.onDestroyView()
    }
}