package ru.maxim.barybians.ui.fragment.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_profile.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Post
import ru.maxim.barybians.model.User
import ru.maxim.barybians.model.response.CommentResponse
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.ui.activity.preferences.PreferencesActivity
import ru.maxim.barybians.ui.activity.profile.ProfileActivity
import ru.maxim.barybians.ui.fragment.base.FeedItem
import ru.maxim.barybians.ui.fragment.base.HeaderItem
import ru.maxim.barybians.ui.fragment.base.PostCreatorItem
import ru.maxim.barybians.ui.fragment.base.PostItem
import ru.maxim.barybians.ui.fragment.base.PostItem.CommentItem
import ru.maxim.barybians.ui.fragment.base.PostItem.UserItem
import ru.maxim.barybians.ui.fragment.feed.FeedRecyclerAdapter.*
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.DialogFactory
import ru.maxim.barybians.utils.toast

class ProfileFragment :
    MvpAppCompatFragment(),
    ProfileView,
    ProfileItemsListener {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter
    private var userId: Int? = null
    private val profileItems = ArrayList<FeedItem>()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? MainActivity)?.supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileRecyclerView.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = arguments?.getInt("userId") ?: PreferencesManager.userId
        profileRefreshLayout.setOnRefreshListener {
            profilePresenter.loadUser(userId?:return@setOnRefreshListener)
        }
        if (savedInstanceState == null)
            profilePresenter.loadUser(userId?:return)
    }

    override fun showNoInternet() {
        profileLoader.visibility = View.GONE
        profileRefreshLayout.isRefreshing = false
        context?.toast(R.string.no_internet_connection)
    }

    override fun showLoading() {
        profileLoader.visibility = View.VISIBLE
    }

    override fun showUserProfile(user: User) {
        val isPersonal = user.id == PreferencesManager.userId
        profileLoader.visibility = View.GONE
        profileRefreshLayout.isRefreshing = false

        profileItems.clear()

        profileItems.add(
            HeaderItem(
                isPersonal,
                user.getAvatarUrl(),
                "${user.firstName} ${user.lastName}",
                user.getRole().iconResource,
                user.getRole().stringResource,
                user.birthDate,
                user.status
            )
        )

        if (isPersonal) profileItems.add(
            PostCreatorItem(
                user.getAvatarUrl(),
                isExpanded = false
            )
        )

        for (post in user.posts) {
            val likes = ArrayList<UserItem>()
            likes.addAll(post.likedUsers.map {
                    UserItem(it.id, "${it.firstName} ${it.lastName}", it.getAvatarUrl())
                })
            val comments = ArrayList<CommentItem>()
            comments.addAll(post.comments.map { comment ->
                    val author = UserItem(
                        comment.author.id,
                        "${comment.author.firstName} ${comment.author.lastName}",
                        comment.author.getAvatarUrl()
                    )
                    val date =
                        DateFormatUtils.getSimplifiedDate(comment.date*1000)
                    CommentItem(comment.id, comment.text, date, author)
                })
            val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
            profileItems.add(
                PostItem(
                    post.id,
                    isPersonal,
                    user.getAvatarUrl(),
                    "${user.firstName} ${user.lastName}",
                    date,
                    post.title,
                    post.text,
                    likes,
                    comments
                )
            )
        }

        profileRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ProfileRecyclerAdapter(profileItems, this@ProfileFragment, this@ProfileFragment)
                .also { it.setHasStableIds(true) }
        }
    }

    override fun onUserLoadError() {
        profileLoader.visibility = View.GONE
        profileRefreshLayout.isRefreshing = false
        context?.toast(R.string.an_error_occurred_while_loading_profile)
    }

    override fun onStatusEdited(newStatus: String?) {
        (profileRecyclerView.findViewHolderForAdapterPosition(0) as? HeaderViewHolder)?.apply {
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
            PostItem(
                post.id, true, PreferencesManager.userAvatar, PreferencesManager.userName, date,
                post.title, post.text, ArrayList(), ArrayList()
            )
        )
        (profileItems[1] as? PostCreatorItem)?.isExpanded = false
        (profileRecyclerView.findViewHolderForAdapterPosition(1)
                as? PostCreatorViewHolder)?.reset()
        profileRecyclerView.adapter?.notifyItemInserted(2)
    }

    override fun onPostCreateError() {
        context?.toast(R.string.unable_to_create_post)
    }

    override fun onPostUpdated(itemPosition: Int, post: Post) {
        val date = DateFormatUtils.getSimplifiedDate(post.date*1000)
        (profileItems[itemPosition] as? PostItem)?.let {
            it.title = post.title
            it.text = post.text
            it.date = date
            profileRecyclerView.adapter?.notifyItemChanged(itemPosition)
        }
    }

    override fun onPostUpdateError() {
        context?.toast(R.string.unable_to_update_post)
    }

    override fun onPostDeleted(itemPosition: Int) {
        profileItems.removeAt(itemPosition)
        profileRecyclerView.adapter?.notifyItemRemoved(itemPosition)
    }

    override fun onPostDeleteError() {
        context?.toast(R.string.unable_to_delete_post)
    }

    override fun onCommentAdded(postPosition: Int, comment: CommentResponse) {
        (activity?.supportFragmentManager?.findFragmentByTag("CommentsBottomSheetFragment") as?
                DialogFactory.CommentBottomSheetFragment)?.addComment(comment)
        profileRecyclerView.adapter?.notifyItemChanged(postPosition)
    }

    override fun onCommentAddError() {
        context?.toast(R.string.unable_to_create_comment)
    }

    override fun onCommentDeleted(postPosition: Int, commentPosition: Int) {
        (activity?.supportFragmentManager?.findFragmentByTag("CommentsBottomSheetFragment") as?
                DialogFactory.CommentBottomSheetFragment)?.deleteComment(commentPosition)
        profileRecyclerView.adapter?.notifyItemChanged(postPosition)
    }

    override fun onCommentDeleteError() {
        context?.toast(R.string.unable_to_delete_comment)
    }

    override fun onLikeEdited(postPosition: Int, likedUsers: ArrayList<User>) {
        val likesList = (profileItems[postPosition] as? PostItem)?.likes
        likesList?.clear()
        likedUsers.forEach {
            likesList?.add(UserItem(it.id, "${it.firstName} ${it.lastName}", it.getAvatarUrl()))
        }
        val postItemViewHolder =
            profileRecyclerView.findViewHolderForAdapterPosition(postPosition)
        (postItemViewHolder as? PostViewHolder)?.invalidateLikes?.invoke()
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

    override fun showDialog(dialogFragment: DialogFragment, tag: String) {
        dialogFragment.show(activity?.supportFragmentManager?:return, tag)
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

    override fun addComment(postId: Int, itemPosition: Int, text: String) {
        profilePresenter.addComment(postId, itemPosition, text)
    }

    override fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) {
        profilePresenter.deleteComment(postPosition, commentId, commentPosition)
    }

    override fun editLike(itemPosition: Int, postId: Int, setLike: Boolean) {
        profilePresenter.editLike(itemPosition, postId, setLike)
    }
}