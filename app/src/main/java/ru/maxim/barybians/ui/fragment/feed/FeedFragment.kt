package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.ui.dialog.PostMenuDialog
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.hide
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class FeedFragment : MvpAppCompatFragment(R.layout.fragment_feed), FeedItemsListener {

    @Inject
    lateinit var viewModelFactory: FeedViewModel.FeedViewModelFactory
    private val model: FeedViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentFeedBinding::bind)

    @Inject
    lateinit var commentsRecyclerAdapter: FeedRecyclerAdapter

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        feedRefreshLayout.setOnRefreshListener { model.refresh() }
        feedRecyclerView.adapter = commentsRecyclerAdapter
        commentsRecyclerAdapter.setFeedItemsListener(this@FeedFragment)

        viewLifecycleOwner.lifecycleScope.launch {
            model.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    feedProgressBar.isVisible =
                        !feedRefreshLayout.isRefreshing &&
                                commentsRecyclerAdapter.currentList.isEmpty()
                } else {
                    feedProgressBar.hide()
                    feedRefreshLayout.isRefreshing = false
                }
            }

            model.messageRes.observe(viewLifecycleOwner) { messageRes ->
                if (commentsRecyclerAdapter.currentList.isEmpty()) {
                    feedMessage.text = messageRes?.let { getString(it) }
                } else {
                    messageRes?.let { context?.toast(it) }
                }
            }

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.feed.collect { posts ->
                    feedMessage.isVisible = posts.isEmpty()
                    commentsRecyclerAdapter.submitList(posts)
                }
            }
        }
    }

    override fun onProfileClick(userId: Int) {
        findNavController().navigate(FeedFragmentDirections.toProfile(userId))
    }

    override fun onImageClick(bitmap: Bitmap) {
        val action = FeedFragmentDirections.toImageViewer(imageBitmap = bitmap)
        findNavController().navigate(action)
    }

    override fun onImageClick(imageUrl: String) {
        val action = FeedFragmentDirections.toImageViewer(imageUrl = imageUrl)
        findNavController().navigate(action)
    }

    override fun onPostMenuClick(postId: Int) {
        commentsRecyclerAdapter.currentList.find { it.id == postId }?.let { post ->
            PostMenuDialog.newInstance(
                title = post.title,
                text = post.text,
                onDelete = {
                    model.deletePost(post.id)
                },
                onEdit = { title, text ->
                    model.editPost(postId, title, text)
                }
            ).show(childFragmentManager, PostMenuDialog::class.simpleName)
        }
    }

    override fun onCommentsClick(postId: Int) {
        val action = FeedFragmentDirections.toCommentsList(postId)
        findNavController().navigate(action)
    }

    override fun onLikeClick(postId: Int) {
        model.changeLike(postId)
    }

    override fun onLikeLongClick(postId: Int) {
        val action = FeedFragmentDirections.toLikesList(postId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        commentsRecyclerAdapter.setFeedItemsListener(null)
    }
}