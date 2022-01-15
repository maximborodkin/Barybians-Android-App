package ru.maxim.barybians.ui.fragment.feed

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_comments_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_feed.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.response.CommentResponse
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.base.FeedItem
import ru.maxim.barybians.ui.fragment.base.ImageViewerFragment
import ru.maxim.barybians.ui.fragment.base.PostItem
import ru.maxim.barybians.ui.fragment.base.PostItem.CommentItem
import ru.maxim.barybians.ui.fragment.base.PostItem.UserItem
import ru.maxim.barybians.utils.*
import javax.inject.Inject
import javax.inject.Provider

class FeedFragment :
    MvpAppCompatFragment(),
    FeedView,
    FeedItemsListener {

    @Inject
    lateinit var presenterProvider: Provider<FeedPresenter>

    private val feedPresenter by moxyPresenter { presenterProvider.get() }

    private val feedItems = ArrayList<FeedItem>()
    private var currentCommentsListDialog: BottomSheetDialog? = null

    @Inject
    lateinit var dateFormatUtils: DateFormatUtils

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_feed, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedRefreshLayout.setOnRefreshListener { feedPresenter.loadFeed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        feedRecyclerView.adapter = null
    }

    override fun showFeed(posts: List<Post>) {
        if (view == null) return
        feedLoading.visibility = View.GONE
        feedRefreshLayout.isRefreshing = false
        feedItems.clear()

        for (post in posts) {
            val user = post.author
            val likes = ArrayList<UserItem>()
            likes.addAll(post.likedUsers.map {
                UserItem(it.id, "${it.firstName} ${it.lastName}", it.avatarMin)
            })
            val comments = ArrayList<CommentItem>()
            comments.addAll(post.comments.map { comment ->
                val author = UserItem(
                    comment.author.id,
                    "${comment.author.firstName} ${comment.author.lastName}",
                    comment.author.avatarMin
                )
                val date =
                    dateFormatUtils.getSimplifiedDate(comment.date * 1000)
                CommentItem(comment.id, comment.text, date, author)
            })

            val date = dateFormatUtils.getSimplifiedDate(post.date * 1000)
            feedItems.add(
                PostItem(
                    post.id,
                    user?.id == preferencesManager.userId,
                    user?.id ?: preferencesManager.userId,
                    user?.avatarMin,
                    "${user?.firstName} ${user?.lastName}",
                    date,
                    post.title,
                    post.text,
                    likes,
                    comments
                )
            )

            if (feedPresenter.currentPostId == post.id && feedPresenter.currentPostPosition != -1) {
                showCommentsList(feedPresenter.currentPostId, feedPresenter.currentPostPosition)
            }
        }
        feedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FeedRecyclerAdapter(
                feedItems,
                preferencesManager.userId,
                this@FeedFragment,
                this@FeedFragment
            ).also { it.setHasStableIds(true) }
        }
    }

    override fun showNoInternet() {
        feedLoading.visibility = View.GONE
        feedRefreshLayout.isRefreshing = false
        context?.toast(R.string.no_internet_connection)
    }

    override fun showLoading() {
        if (!feedRefreshLayout.isRefreshing)
            feedLoading.visibility = View.VISIBLE
    }

    override fun onFeedLoadError() {
        feedLoading.visibility = View.GONE
        feedRefreshLayout.isRefreshing = false
        context?.toast(R.string.an_error_occurred_while_loading_feed)
    }

    override fun onPostUpdated(itemPosition: Int, post: Post) {
        val date = dateFormatUtils.getSimplifiedDate(post.date * 1000)
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

    override fun onCommentAdded(postPosition: Int, comment: CommentResponse) {
        val postComments = (feedItems[postPosition] as? PostItem)?.comments ?: return

        val author = UserItem(
            preferencesManager.userId,
            preferencesManager.userName,
            User.getAvatarMin(preferencesManager.userAvatar)
        )
        val date = dateFormatUtils.getSimplifiedDate(comment.date * 1000)
        val commentItem = CommentItem(comment.id, comment.text, date, author)

        postComments.add(commentItem)
        val commentsCount = postComments.size
        currentCommentsListDialog?.let {
            it.commentsBottomSheetTitle?.text =
                resources.getQuantityString(
                    R.plurals.comment_plurals,
                    commentsCount,
                    commentsCount
                )
            it.commentsBottomSheetEditor?.text = null
            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemInserted(commentsCount)
        }
        feedRecyclerView.adapter?.notifyItemChanged(postPosition)
    }

    override fun onCommentAddError() {
        context?.toast(R.string.unable_to_create_comment)
    }

    override fun onCommentDeleted(postPosition: Int, commentPosition: Int, commentId: Int) {
        val postComments = (feedItems[postPosition] as? PostItem)?.comments ?: return
        if (postComments[commentPosition].id == commentId) postComments.removeAt(commentPosition)
        currentCommentsListDialog?.let {
            it.commentsBottomSheetTitle?.text =
                if (postComments.size > 0)
                    resources.getQuantityString(
                        R.plurals.comment_plurals,
                        postComments.size,
                        postComments.size
                    )
                else
                    getString(R.string.no_comments_yet)
            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemRemoved(commentPosition)
        }
        feedRecyclerView.adapter?.notifyItemChanged(postPosition)
    }

    override fun onCommentDeleteError() {
        context?.toast(R.string.unable_to_delete_comment)
    }

    override fun onLikeEdited(postPosition: Int, likedUsers: List<User>) {
        val likesList = (feedItems[postPosition] as? PostItem)?.likes
        likesList?.clear()
        likedUsers.forEach {
            likesList?.add(
                UserItem(
                    it.id,
                    "${it.firstName} ${it.lastName}",
                    it.avatarMin
                )
            )
        }
        val postItemViewHolder =
            feedRecyclerView.findViewHolderForAdapterPosition(postPosition)
        (postItemViewHolder as? FeedRecyclerAdapter.PostViewHolder)?.invalidateLikes?.invoke()
    }

    override fun openUserProfile(userId: Int) {
//        val profileIntent = Intent(context, ProfileActivity::class.java).apply {
//            putExtra("userId", userId)
//        }
//        startActivity(profileIntent)
        findNavController().navigate(FeedFragmentDirections.toProfile(userId))
    }

    override fun showDialog(dialogFragment: DialogFragment, tag: String) {
        dialogFragment.show(activity?.supportFragmentManager ?: return, tag)
    }

    override fun openImage(drawable: Drawable) {
        ImageViewerFragment
            .newInstance(drawable = drawable)
            .show(activity?.supportFragmentManager ?: return, "ImageViewerFragment")
    }

    override fun openImage(imageUrl: String) {
        ImageViewerFragment
            .newInstance(imageUrl = imageUrl)
            .show(activity?.supportFragmentManager ?: return, "ImageViewerFragment")
    }

    override fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) {
        feedPresenter.editPost(itemPosition, postId, newTitle, newText)
    }

    override fun deletePost(itemPosition: Int, postId: Int) {
        feedPresenter.deletePost(itemPosition, postId)
    }

    private fun addComment(postPosition: Int, postId: Int, text: String) {
        feedPresenter.addComment(postId, postPosition, text)
    }

    private fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) {
        feedPresenter.deleteComment(postPosition, commentId, commentPosition)
    }

    override fun showCommentsList(postId: Int, postPosition: Int) {
        if (postId == -1 || postPosition == -1) return
        feedPresenter.currentPostId = postId
        feedPresenter.currentPostPosition = postPosition
        val commentsListDialog = DialogFactory.createCommentsListDialog(
            context = requireContext(),
            comments = (feedItems[postPosition] as? PostItem)?.comments ?: ArrayList(),
            currentUserId = preferencesManager.userId,
            htmlParser = HtmlParser(lifecycleScope, resources, Glide.with(requireContext())),
            onUserClick = { userId: Int ->
                openUserProfile(userId)
            },
            onImageClick = { drawable: Drawable ->
                openImage(drawable)
            },
            onCommentAdd = { text: String ->
                addComment(postPosition, postId, text)
            },
            onCommentDelete = { commentPosition: Int, commentId: Int ->
                deleteComment(postPosition, commentId, commentPosition)
            }
        )
        commentsListDialog.setOnDismissListener {
            feedPresenter.currentPostId = -1
            feedPresenter.currentPostPosition = -1
            currentCommentsListDialog = null
        }
        currentCommentsListDialog = commentsListDialog
        commentsListDialog.show()
    }

    override fun editLike(itemPosition: Int, postId: Int, setLike: Boolean) {
        feedPresenter.editLike(itemPosition, postId, setLike)
    }
}