package ru.maxim.barybians.ui.fragment.feed

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_comments_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Post
import ru.maxim.barybians.model.User
import ru.maxim.barybians.model.response.CommentResponse
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.profile.ProfileActivity
import ru.maxim.barybians.ui.base.FeedRecyclerAdapter
import ru.maxim.barybians.ui.base.FeedItem
import ru.maxim.barybians.ui.base.FeedItemsListener
import ru.maxim.barybians.ui.base.PostItem
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.toast

class FeedFragment : MvpAppCompatFragment(), FeedView, FeedItemsListener {

    @InjectPresenter
    lateinit var feedPresenter: FeedPresenter
    private val feedItems = ArrayList<FeedItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_feed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedRefreshLayout.setOnRefreshListener { feedPresenter.loadFeed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        feedRecyclerView.adapter = null
    }

    override fun showFeed(posts: ArrayList<Post>) {
        feedRefreshLayout.isRefreshing = false
        feedItems.clear()

        for (post in posts) {
            val user = post.author
            val likes = ArrayList<PostItem.UserItem>()
            likes.addAll(post.likedUsers.map {
                PostItem.UserItem(it.id, "${it.firstName} ${it.lastName}", it.getAvatarUrl())
            })
            val comments = ArrayList<PostItem.CommentItem>()
            comments.addAll(post.comments.map { comment ->
                val author = PostItem.UserItem(
                    comment.author.id,
                    "${comment.author.firstName} ${comment.author.lastName}",
                    comment.author.getAvatarUrl()
                )
                val date =
                    DateFormatUtils.getSimplifiedDate(comment.date*1000)
                PostItem.CommentItem(comment.id, comment.text, date, author)
            })
            val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
            feedItems.add(
                PostItem(
                    post.id, user?.id == PreferencesManager.userId,
                    user?.getAvatarUrl(), "${user?.firstName} ${user?.lastName}", date,
                    post.title, post.text, likes, comments
                )
            )
        }
        feedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FeedRecyclerAdapter(feedItems, this@FeedFragment, this@FeedFragment)
        }
    }

    override fun showNoInternet() {
        feedRefreshLayout.isRefreshing = false
        context?.toast(R.string.no_internet_connection)
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun onFeedLoadError() {
        TODO("Not yet implemented")
    }

    override fun onPostUpdated(itemPosition: Int, post: Post) {
        val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
        (feedItems[itemPosition] as? PostItem)?.let {
            it.title = post.title
            it.text = post.text
            it.date = date
            feedRecyclerView.adapter?.notifyItemChanged(itemPosition)
        }
    }

    override fun onPostUpdateError() {
        context?.toast(R.string.unable_to_update_post)
    }

    override fun onPostDeleted(itemPosition: Int) {
        feedItems.removeAt(itemPosition)
        feedRecyclerView.adapter?.notifyItemRemoved(itemPosition)
    }

    override fun onPostDeleteError() {
        context?.toast(R.string.unable_to_delete_post)
    }

    override fun onCommentAdded(postPosition: Int, commentsCount: Int, comment: CommentResponse) {
        val date = DateFormatUtils.getSimplifiedDate(comment.date*1000)
        val author = PostItem.UserItem(
            PreferencesManager.userId,
            PreferencesManager.userName,
            PreferencesManager.userAvatar
        )
        (feedItems[postPosition] as? PostItem)?.comments?.add(
            PostItem.CommentItem(
                comment.id,
                comment.text,
                date,
                author
            )
        )

        feedRecyclerView.adapter?.notifyItemChanged(postPosition)
        (feedRecyclerView.adapter as? FeedRecyclerAdapter)?.currentBottomSheetDialog?.let {
            it.commentsBottomSheetMessage?.text = resources.getQuantityString(
                R.plurals.comment_plurals, commentsCount + 1, commentsCount + 1)
            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemInserted(commentsCount)
            it.commentsBottomSheetEditor?.text = null
        }
    }

    override fun onCommentAddError() {
        context?.toast(R.string.unable_to_create_comment)
    }

    override fun onCommentDeleted(postPosition: Int, commentsCount: Int, commentPosition: Int) {
        (feedItems[postPosition] as? PostItem)?.comments?.removeAt(commentPosition)
        feedRecyclerView.adapter?.notifyItemChanged(postPosition)
        (feedRecyclerView.adapter as? FeedRecyclerAdapter)?.currentBottomSheetDialog?.let {
            if (commentsCount > 1){
                it.commentsBottomSheetMessage?.text = resources.getQuantityString(
                    R.plurals.comment_plurals, commentsCount - 1, commentsCount - 1)
            } else it.commentsBottomSheetMessage?.text = getString(R.string.no_comments_yet)
            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemRemoved(commentPosition)
        }
    }

    override fun onCommentDeleteError() {
        context?.toast(R.string.unable_to_delete_comment)
    }

    override fun onLikeEdited(postPosition: Int, likedUsers: ArrayList<User>) {
        val likesList = (feedItems[postPosition] as? PostItem)?.likes
        likesList?.clear()
        likedUsers.forEach {
            likesList?.add(
                PostItem.UserItem(
                    it.id,
                    "${it.firstName} ${it.lastName}",
                    it.getAvatarUrl()
                )
            )
        }
        val postItemViewHolder =
            feedRecyclerView.findViewHolderForAdapterPosition(postPosition)
        (postItemViewHolder as? FeedRecyclerAdapter.PostViewHolder)?.invalidateLikes?.invoke()
    }

    override fun openUserProfile(userId: Int) {
        val profileIntent = Intent(context, ProfileActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(profileIntent)
    }

    override fun openImage(drawable: Drawable) {
        TODO("Not yet implemented")
    }

    override fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) {
        feedPresenter.editPost(itemPosition, postId, newTitle, newText)
    }

    override fun deletePost(itemPosition: Int, postId: Int) {
        feedPresenter.deletePost(itemPosition, postId)
    }

    override fun addComment(postId: Int, itemPosition: Int, commentsCount: Int, text: String) {
        feedPresenter.addComment(postId, itemPosition, commentsCount, text)
    }

    override fun deleteComment(postPosition: Int, commentsCount: Int, commentId: Int, commentPosition: Int) {
        feedPresenter.deleteComment(postPosition, commentsCount, commentId, commentPosition)
    }

    override fun editLike(itemPosition: Int, postId: Int, setLike: Boolean) {
        feedPresenter.editLike(itemPosition, postId, setLike)
    }
}