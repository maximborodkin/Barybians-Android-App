package ru.maxim.barybians.ui.fragment.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.FragmentProfileBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.base.*
import ru.maxim.barybians.ui.fragment.base.PostItem.CommentItem
import ru.maxim.barybians.ui.fragment.base.PostItem.UserItem
import ru.maxim.barybians.ui.fragment.feed.FeedRecyclerAdapter.*
import ru.maxim.barybians.utils.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment :
    MvpAppCompatFragment()
//    ,ProfileView,
//    ProfileItemsListener {
//
//    @Inject
//    lateinit var preferencesManager: PreferencesManager
//
//    @Inject
//    lateinit var dateFormatUtils: DateFormatUtils
//
//    @Inject
//    lateinit var presenterProvider: Provider<ProfilePresenter>
//
//    private val profilePresenter by moxyPresenter { presenterProvider.get() }
//
//    private val args: ProfileFragmentArgs by navArgs()
//    private val userId: Int by lazy {
//        if (args.userId != 0) args.userId else preferencesManager.userId
//    }
//    private lateinit var binding: FragmentProfileBinding
//
//    private val profileItems = ArrayList<FeedItem>()
//    private var currentCommentsListDialog: BottomSheetDialog? = null
//    private var editStatusDialog: AlertDialog? = null
//    private var isEditStatusDialogShown = false
//    private var currentEditableStatus: String? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        context.appComponent.inject(this)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentProfileBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.profileRefreshLayout.setOnRefreshListener {
//            profilePresenter.loadUser(userId)
//        }
//
//        if (savedInstanceState == null) {
//            profilePresenter.loadUser(userId)
//        } else {
//            isEditStatusDialogShown =
//                savedInstanceState.getBoolean("isEditStatusDialogShown", false)
//
//            if (isEditStatusDialogShown) {
//                val currentEditableStatus =
//                    savedInstanceState.getString("currentEditableStatus")
//                showEditStatusDialog(currentEditableStatus)
//            }
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean("isEditStatusDialogShown", isEditStatusDialogShown)
//        if (currentEditableStatus.isNotNull())
//            outState.putString("currentEditableStatus", currentEditableStatus)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding.profileRecyclerView.adapter = null
//        editStatusDialog?.cancel()
//    }
//
//    override fun showNoInternet() {
//        binding.profileLoading.visibility = View.GONE
//        binding.profileRefreshLayout.isRefreshing = false
//        context?.toast(R.string.no_internet_connection)
//    }
//
//    override fun showLoading() {
//        if (!binding.profileRefreshLayout.isRefreshing)
//            binding.profileLoading.visibility = View.VISIBLE
//    }
//
//    override fun showUserProfile(user: User) {
//        val isPersonal = user.id == preferencesManager.userId
//        binding.profileLoading.visibility = View.GONE
//        binding.profileRefreshLayout.isRefreshing = false
//
//        profileItems.clear()
//
//        profileItems.add(
//            HeaderItem(
//                userId = user.id,
//                isPersonal = isPersonal,
//                avatarSmall = user.avatarMin,
//                avatarFull = user.avatarFull,
//                name = user.fullName,
//                roleDrawable = user.getRole().iconResource,
//                roleDescription = user.getRole().stringResource,
//                birthDate = user.birthDate,
//                status = user.status
//            )
//        )
//
//        if (isPersonal) profileItems.add(PostCreatorItem(user.avatarMin, isExpanded = false))
//
//        for (post in user.posts) {
//            val likes = ArrayList<UserItem>()
//            likes.addAll(post.likedUsers.map {
//                UserItem(it.id, "${it.firstName} ${it.lastName}", it.avatarMin)
//            })
//            val comments: ArrayList<CommentItem> = ArrayList()
//            comments.addAll(post.comments.map { comment ->
//                val author = UserItem(
//                    comment.author.id,
//                    "${comment.author.firstName} ${comment.author.lastName}",
//                    comment.author.avatarMin
//                )
//                val date = dateFormatUtils.getSimplifiedDate(comment.date * 1000)
//                CommentItem(comment.id, comment.text, date, author)
//            })
//
//            val date = dateFormatUtils.getSimplifiedDate(post.date * 1000)
//            profileItems.add(
//                PostItem(
//                    postId = post.id,
//                    isPersonal = isPersonal,
//                    authorId = user.id,
//                    avatar = user.avatarMin,
//                    name = user.fullName,
//                    date = date,
//                    title = post.title,
//                    text = post.text,
//                    likes = likes,
//                    comments = comments
//                )
//            )
//
//            val currentPostId = profilePresenter.currentPostId
//            val currentPostPosition = profilePresenter.currentPostPosition
//            if (currentPostId == post.id && currentPostPosition != -1) {
//                showCommentsList(currentPostId, currentPostPosition)
//            }
//        }
//
//        binding.profileRecyclerView.adapter =
//            ProfileRecyclerAdapter(
//                feedItems = profileItems,
//                profileItemsListener = this@ProfileFragment,
//                preferencesManager.userId,
//                lifecycleOwner = this@ProfileFragment
//            )
//                .also { it.setHasStableIds(true) }
//
//    }
//
//    override fun onUserLoadError() {
//        binding.profileLoading.visibility = View.GONE
//        binding.profileRefreshLayout.isRefreshing = false
//        context?.toast(R.string.an_error_occurred_while_loading_profile)
//    }
//
//    override fun onStatusEdited(newStatus: String?) {
//        (binding.profileRecyclerView.findViewHolderForAdapterPosition(0) as? HeaderViewHolder)?.apply {
//            if (!newStatus.isNullOrBlank()) {
//                statusView.text = newStatus
//                statusView.clearDrawables()
//            } else {
//                statusView.text = itemView.context.getString(R.string.enter_your_status)
//                statusView.setDrawableEnd(R.drawable.ic_edit_grey)
//            }
//        }
//    }
//
//    override fun onPostCreated(post: Post) {
//        val date = dateFormatUtils.getSimplifiedDate(post.date * 1000)
//        profileItems.add(
//            2,
//            PostItem(
//                post.id,
//                true,
//                preferencesManager.userId,
//                User.getAvatarMin(preferencesManager.userAvatar),
//                preferencesManager.userName,
//                date,
//                post.title,
//                post.text,
//                ArrayList(),
//                ArrayList()
//            )
//        )
//        (profileItems[1] as? PostCreatorItem)?.isExpanded = false
//        (binding.profileRecyclerView.findViewHolderForAdapterPosition(1)
//                as? PostCreatorViewHolder)?.reset()
//        binding.profileRecyclerView.adapter?.notifyItemInserted(2)
//    }
//
//    override fun onPostCreateError() {
//        context?.toast(R.string.unable_to_create_post)
//    }
//
//    override fun onPostUpdated(itemPosition: Int, post: Post) {
//        val date = dateFormatUtils.getSimplifiedDate(post.date * 1000)
//        (profileItems[itemPosition] as? PostItem)?.let {
//            it.title = post.title
//            it.text = post.text
//            it.date = date
//            binding.profileRecyclerView.adapter?.notifyItemChanged(itemPosition)
//        }
//    }
//
//    override fun onPostUpdateError() {
//        context?.toast(R.string.unable_to_update_post)
//    }
//
//    override fun onPostDeleted(itemPosition: Int) {
//        profileItems.removeAt(itemPosition)
//        binding.profileRecyclerView.adapter?.notifyItemRemoved(itemPosition)
//    }
//
//    override fun onPostDeleteError() {
//        context?.toast(R.string.unable_to_delete_post)
//    }
//
//    override fun onCommentAdded(postPosition: Int, comment: Comment) {
//        val postComments = (profileItems[postPosition] as? PostItem)?.comments ?: return
//
//        val author = UserItem(
//            preferencesManager.userId,
//            preferencesManager.userName,
//            User.getAvatarMin(preferencesManager.userAvatar)
//        )
//        val date = dateFormatUtils.getSimplifiedDate(comment.date * 1000)
//        val commentItem = CommentItem(comment.id, comment.text, date, author)
//
//        postComments.add(commentItem)
//        val commentsCount = postComments.size
//        // TODO: migrate to flow
////        currentCommentsListDialog?.let {
////            it.commentsBottomSheetTitle?.text =
////                resources.getQuantityString(
////                    R.plurals.comment_plurals,
////                    commentsCount,
////                    commentsCount
////                )
////            it.commentsBottomSheetEditor?.text = null
////            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemInserted(commentsCount)
////        }
////        feedRecyclerView.adapter?.notifyItemChanged(postPosition)
//    }
//
//    override fun onCommentAddError() {
//        context?.toast(R.string.unable_to_create_comment)
//    }
//
//    override fun onCommentEdit(comment: Comment) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCommentEditError() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCommentDeleted(postPosition: Int, commentPosition: Int, commentId: Int) {
//        val postComments = (profileItems[postPosition] as? PostItem)?.comments ?: return
//        if (postComments[commentPosition].id == commentId) postComments.removeAt(commentPosition)
//        // TODO: migrate to flow
////        currentCommentsListDialog?.let {
////            it.commentsBottomSheetTitle?.text =
////                if (postComments.size > 0)
////                    resources.getQuantityString(R.plurals.comment_plurals, postComments.size, postComments.size)
////                else
////                    getString(R.string.no_comments_yet)
////            it.commentsBottomSheetRecyclerView?.adapter?.notifyItemRemoved(commentPosition)
////        }
////        feedRecyclerView.adapter?.notifyItemChanged(postPosition)
//    }
//
//    override fun onCommentDeleteError() {
//        context?.toast(R.string.unable_to_delete_comment)
//    }
//
//    override fun onLikeEdited(postPosition: Int, likedUsers: List<User>) {
//        val likesList = (profileItems[postPosition] as? PostItem)?.likes
//        likesList?.clear()
//        likedUsers.forEach {
//            likesList?.add(UserItem(it.id, "${it.firstName} ${it.lastName}", it.avatarMin))
//        }
//        val postItemViewHolder =
//            binding.profileRecyclerView.findViewHolderForAdapterPosition(postPosition)
//        (postItemViewHolder as? PostViewHolder)?.invalidateLikes?.invoke()
//    }
//
//    override fun onLikeEditError() {
//        TODO("Not yet implemented")
//    }
//
//    /* ProfileItemsListener */
////    override fun popBackStack() {
////        activity?.finish()
////    }
//
//    override fun openPreferences() {
//        findNavController().navigate(ProfileFragmentDirections.profileToSettings())
////        startActivity(Intent(context, PreferencesActivity::class.java))
//    }
//
//    override fun onProfileClick(userId: Int) {
////        val profileIntent = Intent(context, ProfileActivity::class.java).apply {
////            putExtra("userId", userId)
////        }
////        startActivity(profileIntent)
//        findNavController().navigate(ProfileFragmentDirections.toProfile(userId))
//    }
//
//    override fun showDialog(dialogFragment: DialogFragment, tag: String) {
//        dialogFragment.show(activity?.supportFragmentManager ?: return, tag)
//    }
//
//    override fun onImageClick(drawable: Drawable) {
//        ImageViewerFragment
//            .newInstance(drawable = drawable)
//            .show(activity?.supportFragmentManager ?: return, "ImageViewerFragment")
//    }
//
//    override fun onImageClick(imageUrl: String) {
//        ImageViewerFragment
//            .newInstance(imageUrl = imageUrl)
//            .show(activity?.supportFragmentManager ?: return, "ImageViewerFragment")
//    }
//
//    override fun openDialog(userId: Int, userAvatar: String?, userName: String) {
////        startActivity(
////            Intent(context, DialogFragment::class.java).apply {
////                putExtra("userId", userId)
////                putExtra("userAvatar", userAvatar)
////                putExtra("userName", userName)
////            }
////        )
//        findNavController().navigate(ProfileFragmentDirections.toChat(userId))
//    }
//
//    override fun showEditStatusDialog(status: String?) {
//        var currentStatus = status
//        editStatusDialog = DialogFactory.createEditStatusDialog(context ?: return, currentStatus,
//            onStatusEdited = {
//                currentStatus = it
//            }, onStatusEditConfirmed = {
//                profilePresenter.editStatus(currentStatus)
//            }
//        ).apply {
//            setOnDismissListener { isEditStatusDialogShown = false }
//            setOnCancelListener { currentEditableStatus = currentStatus }
//        }
//        editStatusDialog?.show()
//        isEditStatusDialogShown = true
//    }
//
//    override fun editUserInfo() {
//        TODO("Not yet implemented")
//    }
//
//    override fun addPost(title: String?, text: String) {
//        profilePresenter.createPost(title, text)
//    }
//
//    override fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String) {
//        profilePresenter.editPost(itemPosition, postId, newTitle, newText)
//    }
//
//    override fun deletePost(itemPosition: Int, postId: Int) {
//        profilePresenter.deletePost(itemPosition, postId)
//    }
//
//    private fun addComment(postPosition: Int, postId: Int, text: String) {
//        profilePresenter.createComment(postId, postPosition, text)
//    }
//
//    private fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int) {
//        profilePresenter.deleteComment(postPosition, commentId, commentPosition)
//    }
//
//    override fun showCommentsList(postId: Int, postPosition: Int) {
//        if (postId == -1 || postPosition == -1) return
//        profilePresenter.currentPostId = postId
//        profilePresenter.currentPostPosition = postPosition
//        val commentsListDialog = DialogFactory.createCommentsListDialog(
//            context = requireContext(),
//            comments = (profileItems[postPosition] as? PostItem)?.comments ?: ArrayList(),
//            htmlParser = HtmlParser(lifecycleScope, resources, Glide.with(requireContext())),
//            currentUserId = preferencesManager.userId,
//            onUserClick = { userId: Int ->
//                onProfileClick(userId)
//            },
//            onImageClick = { drawable: Drawable ->
//                onImageClick(drawable)
//            },
//            onCommentAdd = { text: String ->
//                addComment(postPosition, postId, text)
//            },
//            onCommentDelete = { commentPosition: Int, commentId: Int ->
//                deleteComment(postPosition, commentId, commentPosition)
//            }
//        )
//        commentsListDialog.setOnDismissListener {
//            profilePresenter.currentPostId = -1
//            profilePresenter.currentPostPosition = -1
//            currentCommentsListDialog = null
//        }
//        currentCommentsListDialog = commentsListDialog
//        commentsListDialog.show()
//    }
//
//    override fun editLike(itemPosition: Int, postId: Int, setLike: Boolean) {
//        profilePresenter.editLike(itemPosition, postId, setLike)
//    }
//}