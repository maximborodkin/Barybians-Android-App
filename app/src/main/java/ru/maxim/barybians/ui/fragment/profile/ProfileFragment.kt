package ru.maxim.barybians.ui.fragment.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_comments_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Post
import ru.maxim.barybians.model.User
import ru.maxim.barybians.model.response.CommentResponse
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.ui.activity.preferences.PreferencesActivity
import ru.maxim.barybians.ui.activity.profile.ProfileActivity
import ru.maxim.barybians.ui.fragment.profile.ProfileItemPost.ItemComment
import ru.maxim.barybians.ui.fragment.profile.ProfileItemPost.ItemUser
import ru.maxim.barybians.ui.fragment.profile.ProfileRecyclerAdapter.*
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.toast

class ProfileFragment :
    MvpAppCompatFragment(),
    ProfileView,
    ProfileItemsListener {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter
    private var userId: Int? = null
    private val profileItems = ArrayList<ProfileItem>()
    private lateinit var profileRecyclerAdapter: ProfileRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? MainActivity)?.supportActionBar?.hide()
        profileRecyclerAdapter = ProfileRecyclerAdapter(profileItems, this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = arguments?.getInt("userId") ?: PreferencesManager.userId
        profilePresenter.loadUser(userId?:return)
        profileRefreshLayout.setOnRefreshListener {
            profilePresenter.loadUser(userId?:return@setOnRefreshListener)
        }
    }

    override fun showNoInternet() {
        profileLoader.visibility = View.GONE
        profileRefreshLayout.isRefreshing = false
        context?.toast(getString(R.string.no_internet_connection))
    }

    override fun showLoading() {
        profileLoader.visibility = View.VISIBLE
    }

    override fun showUserProfile(user: User) {
        profileLoader.visibility = View.GONE
        profileRefreshLayout.isRefreshing = false

        profileItems.clear()
        profileItems.add(
            ProfileItemHeader(
                userId == PreferencesManager.userId,
                user.getAvatarUrl(),
                "${user.firstName} ${user.lastName}",
                user.getRole().iconResource,
                user.getRole().stringResource,
                user.birthDate,
                user.status)
        )

        if (userId == PreferencesManager.userId)
            profileItems.add(ProfileItemPostCreator(user.getAvatarUrl(), isExpanded = false))

        for (post in user.posts) {
            val likes = ArrayList<ItemUser>()
            likes.addAll(post.likedUsers.map {
                    ItemUser(it.id, "${it.firstName} ${it.lastName}", it.getAvatarUrl())
                })
            val comments = ArrayList<ItemComment>()
            comments.addAll(post.comments.map { comment ->
                    val author = ItemUser(
                        comment.author.id,
                        "${comment.author.firstName} ${comment.author.lastName}",
                        comment.author.getAvatarUrl()
                    )
                    val date =
                        DateFormatUtils.getSimplifiedDate(comment.date*1000)
                    ItemComment(comment.id, comment.text, date, author)
                })
            val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
            profileItems.add(ProfileItemPost(post.id, userId == PreferencesManager.userId,
                user.getAvatarUrl(), "${user.firstName} ${user.lastName}", date,
                post.title, post.text, likes, comments))
        }

        profileRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = profileRecyclerAdapter
        }
    }

    override fun onUserLoadError() {
        profileLoader.visibility = View.GONE
        profileRefreshLayout.isRefreshing = false
        context?.toast(getString(R.string.an_error_occurred_while_loading_profile))
    }

    override fun onStatusEdited(newStatus: String?) {
        (profileRecyclerView.findViewHolderForAdapterPosition(0) as? ProfileHeaderViewHolder)?.apply {
            if (!newStatus.isNullOrBlank()) {
                statusView.text = newStatus
                statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            } else {
                statusView.text = itemView.context.getString(R.string.enter_your_status)
                statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit_grey, 0)
            }
        }
    }

    override fun onPostCreated(post: Post) {
        val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
        profileItems.add(2,
            ProfileItemPost(
                post.id, true, PreferencesManager.userAvatar, PreferencesManager.userName, date,
                post.title, post.text, ArrayList(), ArrayList()
            )
        )
        (profileItems[1] as? ProfileItemPostCreator)?.isExpanded = false
        (profileRecyclerView.findViewHolderForAdapterPosition(1) as? ProfilePostCreatorViewHolder)?.reset()
        profileRecyclerAdapter.notifyItemInserted(2)
    }

    override fun onPostCreateError() {
        Toast.makeText(context, getString(R.string.unable_to_create_post), Toast.LENGTH_SHORT).show()
    }

    override fun onPostUpdated(itemPosition: Int, post: Post) {
        val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
        (profileItems[itemPosition] as? ProfileItemPost)?.let {
            it.title = post.title
            it.text = post.text
            it.date = date
            profileRecyclerAdapter.notifyItemChanged(itemPosition)
        }
    }

    override fun onPostUpdateError() {
        context?.toast(getString(R.string.unable_to_update_post))
    }

    override fun onPostDeleted(itemPosition: Int) {
        profileItems.removeAt(itemPosition)
        profileRecyclerAdapter.notifyItemRemoved(itemPosition)
    }

    override fun onPostDeleteError() {
        context?.toast(getString(R.string.unable_to_delete_post))
    }

    override fun onCommentAdded(postPosition: Int, commentsCount: Int, comment: CommentResponse) {
        val date = DateFormatUtils.getSimplifiedDate(comment.date*1000)
        val author = ItemUser(
            PreferencesManager.userId,
            PreferencesManager.userName,
            PreferencesManager.userAvatar
        )
        (profileItems[postPosition] as? ProfileItemPost)?.comments?.add(ItemComment(comment.id, comment.text, date, author))

        profileRecyclerAdapter.notifyItemChanged(postPosition)
        profileRecyclerAdapter.currentBottomSheetDialog?.let {
            it.commentsBottomSheetMessage?.text = resources.getQuantityString(
                R.plurals.comment_plurals, commentsCount + 1, commentsCount + 1)
            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemInserted(commentsCount)
            it.commentsBottomSheetEditor?.text = null
        }
    }

    override fun onCommentAddError() {
        Toast.makeText(context, getString(R.string.unable_to_create_comment), Toast.LENGTH_SHORT).show()
    }

    override fun onCommentDeleted(postPosition: Int, commentsCount: Int, commentPosition: Int) {
        (profileItems[postPosition] as? ProfileItemPost)?.comments?.removeAt(commentPosition)
        profileRecyclerAdapter.notifyItemChanged(postPosition)
        profileRecyclerAdapter.currentBottomSheetDialog?.let {
            if (commentsCount > 1){
                it.commentsBottomSheetMessage?.text = resources.getQuantityString(
                    R.plurals.comment_plurals, commentsCount - 1, commentsCount - 1)
            } else it.commentsBottomSheetMessage?.text = getString(R.string.no_comments_yet)
            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemRemoved(commentPosition)
        }
    }

    override fun onCommentDeleteError() {
        Toast.makeText(context, getString(R.string.unable_to_delete_comment), Toast.LENGTH_SHORT).show()
    }

    override fun onLikeEdited(postPosition: Int, likedUsers: ArrayList<User>) {
        val likesList = (profileItems[postPosition] as? ProfileItemPost)?.likes
        likesList?.clear()
        likedUsers.forEach {
            likesList?.add(ItemUser(it.id, "${it.firstName} ${it.lastName}", it.getAvatarUrl()))
        }
        val postItemViewHolder =
            profileRecyclerView.findViewHolderForAdapterPosition(postPosition)
        (postItemViewHolder as? ProfilePostViewHolder)?.invalidateLikes?.invoke()
    }

    /* ProfileItemsListener */
    override fun popBackStack() {
        activity?.finish()
    }

    override fun openPreferences() {
        startActivity(Intent(context, PreferencesActivity::class.java))
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

    override fun openDialog(userId: Int) {
        TODO("Not yet implemented")
    }

    override fun editStatus(newStatus: String?) {
        profilePresenter.editStatus(newStatus)
    }

    override fun editUserInfo() {
        TODO("Not yet implemented")
    }

    override fun addPost(title: String?, text: String) {
        profilePresenter.createPost(title, text)
    }

    override fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) {
        profilePresenter.editPost(itemPosition, postId, newTitle, newText)
    }

    override fun deletePost(itemPosition: Int, postId: Int) {
        profilePresenter.deletePost(itemPosition, postId)
    }

    override fun addComment(postId: Int, itemPosition: Int, commentsCount: Int, text: String) {
        profilePresenter.addComment(postId, itemPosition, commentsCount, text)
    }

    override fun deleteComment(postPosition: Int, commentsCount: Int, commentId: Int, commentPosition: Int) {
        profilePresenter.deleteComment(postPosition, commentsCount, commentId, commentPosition)
    }

    override fun editLike(itemPosition: Int, postId: Int, setLike: Boolean) {
        profilePresenter.editLike(itemPosition, postId, setLike)
    }
}