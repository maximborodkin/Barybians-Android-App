package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.dialog.CommentsListDialog
import ru.maxim.barybians.ui.dialog.PostMenuDialog
import ru.maxim.barybians.ui.fragment.base.ImageViewerFragment
import ru.maxim.barybians.utils.*
import javax.inject.Inject
import javax.inject.Provider

class FeedFragment : MvpAppCompatFragment(R.layout.fragment_feed), FeedView, FeedItemsListener {

    @Inject
    lateinit var presenterProvider: Provider<FeedPresenter>
    private val feedPresenter by moxyPresenter { presenterProvider.get() }

    private val binding by viewBinding(FragmentFeedBinding::bind)
    private var recyclerAdapter by autoCleared<FeedRecyclerAdapter>()

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.feedRefreshLayout.setOnRefreshListener { feedPresenter.loadFeed() }
        recyclerAdapter = FeedRecyclerAdapter(
            currentUserId = preferencesManager.userId,
            feedItemsListener = this,
            dateFormatUtils = dateFormatUtils
        )
        binding.feedRecyclerView.adapter = recyclerAdapter
    }

    override fun showFeed(posts: List<Post>) = with(binding) {
        feedLoading.isVisible = false
        feedRefreshLayout.isRefreshing = false
        recyclerAdapter.submitList(posts)
    }

    override fun showNoInternet() = with(binding) {
        context?.toast(R.string.no_internet_connection)
        feedLoading.visibility = View.GONE
        feedRefreshLayout.isRefreshing = false
    }

    override fun showLoading() = with(binding) {
        if (!feedRefreshLayout.isRefreshing)
            feedLoading.visibility = View.VISIBLE
    }

    override fun onFeedLoadError() = with(binding) {
        context?.toast(R.string.an_error_occurred_while_loading_feed)
        feedLoading.visibility = View.GONE
        feedRefreshLayout.isRefreshing = false
    }

    override fun onPostUpdated(post: Post) {
        recyclerAdapter.currentList.indexOrNull { it.id == post.id }?.let { index ->
            recyclerAdapter.currentList[index] = post
            recyclerAdapter.notifyItemChanged(index)
            // TODO Update all opened dialogs for this post
        }
    }

    override fun onPostUpdateError() {
        context?.toast(R.string.unable_to_update_post)
    }

    override fun onPostDeleted(postId: Int) {
        recyclerAdapter.currentList.indexOrNull { it.id == postId }?.let { postIndex ->
            recyclerAdapter.currentList.removeAt(postIndex)
            recyclerAdapter.notifyItemRemoved(postIndex)
            // TODO Close all opened dialogs for this post
        }
    }

    override fun onPostDeleteError() {
        context?.toast(R.string.unable_to_delete_post)
    }

    override fun onCommentAdded(postId: Int, comment: Comment) {
        recyclerAdapter.currentList.indexOrNull { it.id == postId }?.let { postIndex ->
            val postComments = recyclerAdapter.currentList[postIndex].comments
            postComments.add(comment)
            recyclerAdapter.notifyItemChanged(postIndex)
            // TODO Update opened dialog with comments
        }
    }

    override fun onCommentAddError() {
        context?.toast(R.string.unable_to_create_comment)
    }

    override fun onCommentEdit(comment: Comment) {
        recyclerAdapter.currentList.indexOrNull { post ->
            post.comments.contains { it.id == comment.id }
        }?.let { postIndex ->
            val postComments = recyclerAdapter.currentList[postIndex].comments
            postComments.indexOrNull { it.id == comment.id }?.let { commentIndex ->
                postComments[commentIndex] = comment
                recyclerAdapter.notifyItemChanged(postIndex)
                // TODO Update opened dialog with comments
            }
        }
    }

    override fun onCommentEditError() {
        context?.toast(R.string.unable_to_update_comment)
    }

    override fun onCommentDeleted(commentId: Int) {
        recyclerAdapter.currentList.indexOrNull { post ->
            post.comments.contains { it.id == commentId }
        }?.let { postIndex ->
            val postComments = recyclerAdapter.currentList[postIndex].comments
            postComments.indexOrNull { it.id == commentId }?.let { commentIndex ->
                postComments.removeAt(commentIndex)
                recyclerAdapter.notifyItemChanged(postIndex)
                // TODO Update opened dialog with comments
            }
        }
    }

    override fun onCommentDeleteError() {
        context?.toast(R.string.unable_to_delete_comment)
    }

    override fun onLikeEdited(postId: Int, likedUsers: List<User>) {
        recyclerAdapter.currentList.indexOrNull { it.id == postId }?.let { postIndex ->
            recyclerAdapter.currentList[postIndex].likedUsers.apply {
                clear()
                addAll(likedUsers)
            }
            recyclerAdapter.notifyItemChanged(postIndex)
            // TODO Update opened dialog with liked users
        }
    }

    override fun onLikeEditError() {
        context?.toast(R.string.unable_to_update_like)
    }

    override fun onProfileClick(userId: Int) {
        findNavController().navigate(FeedFragmentDirections.toProfile(userId))
    }

    override fun onImageClick(drawable: Drawable) {
        ImageViewerFragment
            .newInstance(drawable = drawable)
            .show(childFragmentManager, "ImageViewerFragment")
    }

    override fun onImageClick(imageUrl: String) {
        ImageViewerFragment
            .newInstance(imageUrl = imageUrl)
            .show(childFragmentManager, "ImageViewerFragment")
    }

    override fun onPostMenuClick(postId: Int) {
//        recyclerAdapter.currentList.find { it.id == postId }?.let { post ->
//            PostMenuDialog.newInstance(
//                title = post.title,
//                text = post.text,
//                onDelete = { feedPresenter.deletePost(post.id) },
//                onEdit = { title, text ->
//                    feedPresenter.editPost(postId, title, text)
//                }
//            ).show(childFragmentManager, PostMenuDialog::class.simpleName)
//        }
    }

    override fun onCommentsClick(postId: Int) {
//        recyclerAdapter.currentList.find { it.id == postId }?.let { post ->
//            CommentsListDialog.newInstance(
//                comments = post.comments,
//                onUserClick = ::onProfileClick,
//                onImageClick = ::onImageClick,
//                onCommentAdd = { text -> feedPresenter.createComment(post.id, text) },
//                onCommentEdit = feedPresenter::editComment,
//                onCommentDelete = feedPresenter::deleteComment
//            ).show(childFragmentManager, CommentsListDialog::class.simpleName)
//        }
    }

    override fun onLikeClick(postId: Int, hasPersonalLike: Boolean) {
        feedPresenter.editLike(postId, !hasPersonalLike)
    }

    override fun onLikeLongClick(postId: Int) {
        TODO("Not yet implemented")
    }
}