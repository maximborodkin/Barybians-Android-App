package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentFeedBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.dialog.PostMenuDialog
import ru.maxim.barybians.utils.*
import javax.inject.Inject
import javax.inject.Provider

class FeedFragment : MvpAppCompatFragment(R.layout.fragment_feed), FeedView, FeedItemsListener {

    @Inject
    lateinit var presenterProvider: Provider<FeedPresenter>
    private val feedPresenter by moxyPresenter { presenterProvider.get() }

    private val binding by viewBinding(FragmentFeedBinding::bind)

    @Inject
    lateinit var commentsRecyclerAdapter: FeedRecyclerAdapter

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.feedRefreshLayout.setOnRefreshListener { feedPresenter.loadFeed() }
        binding.feedRecyclerView.adapter = commentsRecyclerAdapter
        commentsRecyclerAdapter.setFeedItemsListener(this)
    }

    override fun showFeed(posts: List<Post>) = with(binding) {
        feedLoading.hide()
        feedRefreshLayout.isRefreshing = false
        commentsRecyclerAdapter.submitList(posts)
    }

    override fun showNoInternet() = with(binding) {
        context?.toast(R.string.no_internet_connection)
        feedLoading.hide()
        feedRefreshLayout.isRefreshing = false
    }

    override fun showLoading() = with(binding) {
        if (!feedRefreshLayout.isRefreshing)
            feedLoading.show()
    }

    override fun onFeedLoadError() = with(binding) {
        context?.toast(R.string.an_error_occurred_while_loading_feed)
        feedLoading.hide()
        feedRefreshLayout.isRefreshing = false
    }

    override fun onPostUpdated(post: Post) {
        commentsRecyclerAdapter.currentList.indexOrNull { it.id == post.id }?.let { index ->
            commentsRecyclerAdapter.currentList[index] = post
            commentsRecyclerAdapter.notifyItemChanged(index)
        }
    }

    override fun onPostUpdateError() {
        context?.toast(R.string.unable_to_update_post)
    }

    override fun onPostDeleted(postId: Int) {
        commentsRecyclerAdapter.currentList.indexOrNull { it.id == postId }?.let { postIndex ->
            commentsRecyclerAdapter.currentList.removeAt(postIndex)
            commentsRecyclerAdapter.notifyItemRemoved(postIndex)
        }
    }

    override fun onPostDeleteError() {
        context?.toast(R.string.unable_to_delete_post)
    }

//    override fun onCommentAdded(postId: Int, comment: Comment) {
//        commentsRecyclerAdapter.currentList.indexOrNull { it.id == postId }?.let { postIndex ->
//            val postComments = commentsRecyclerAdapter.currentList[postIndex].comments
//            postComments.add(comment)
//            commentsRecyclerAdapter.notifyItemChanged(postIndex)
//        }
//    }
//
//    override fun onCommentAddError() {
//        context?.toast(R.string.unable_to_create_comment)
//    }
//
//    override fun onCommentEdit(comment: Comment) {
//        commentsRecyclerAdapter.currentList.indexOrNull { post ->
//            post.comments.contains { it.id == comment.id }
//        }?.let { postIndex ->
//            val postComments = commentsRecyclerAdapter.currentList[postIndex].comments
//            postComments.indexOrNull { it.id == comment.id }?.let { commentIndex ->
//                postComments[commentIndex] = comment
//                commentsRecyclerAdapter.notifyItemChanged(postIndex)
//            }
//        }
//    }
//
//    override fun onCommentEditError() {
//        context?.toast(R.string.unable_to_update_comment)
//    }
//
//    override fun onCommentDeleted(commentId: Int) {
//        commentsRecyclerAdapter.currentList.indexOrNull { post ->
//            post.comments.contains { it.id == commentId }
//        }?.let { postIndex ->
//            val postComments = commentsRecyclerAdapter.currentList[postIndex].comments
//            postComments.indexOrNull { it.id == commentId }?.let { commentIndex ->
//                postComments.removeAt(commentIndex)
//                commentsRecyclerAdapter.notifyItemChanged(postIndex)
//            }
//        }
//    }
//
//    override fun onCommentDeleteError() {
//        context?.toast(R.string.unable_to_delete_comment)
//    }

    override fun onLikeEdited(postId: Int, likedUsers: List<User>) {
        commentsRecyclerAdapter.currentList.indexOrNull { it.id == postId }?.let { postIndex ->
            commentsRecyclerAdapter.currentList[postIndex].likedUsers = likedUsers
            commentsRecyclerAdapter.notifyItemChanged(postIndex)
        }
    }

    override fun onLikeEditError() {
        context?.toast(R.string.unable_to_update_like)
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
                onDelete = { feedPresenter.deletePost(post.id) },
                onEdit = { title, text ->
                    feedPresenter.editPost(postId, title, text)
                }
            ).show(childFragmentManager, PostMenuDialog::class.simpleName)
        }
    }

    override fun onCommentsClick(postId: Int) {
        val action = FeedFragmentDirections.toCommentsList(postId)
        findNavController().navigate(action)
    }

    override fun onLikeClick(postId: Int, hasPersonalLike: Boolean) {
        feedPresenter.editLike(postId, !hasPersonalLike)
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