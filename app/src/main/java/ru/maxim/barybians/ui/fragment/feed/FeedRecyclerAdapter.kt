package ru.maxim.barybians.ui.fragment.feed

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_post_creator.view.*
import kotlinx.android.synthetic.main.item_profile_header.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.fragment.base.*
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.DialogFactory
import ru.maxim.barybians.utils.HtmlParser
import ru.maxim.barybians.utils.weak


open class FeedRecyclerAdapter(
    private val feedItems: ArrayList<FeedItem>,
    private val feedItemsListener: FeedItemsListener,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    final override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setItemViewCacheSize(20)
    }

    final override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

    }

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var isBinded = false
        val backgroundView: ImageView = view.itemProfileHeaderImageBackground
        val backBtn: AppCompatImageView = view.itemProfileHeaderBack
        val preferencesBtn: AppCompatImageView = view.itemProfileHeaderPreferences
        val editBtn: AppCompatImageView = view.itemProfileHeaderEdit
        val avatarView: AvatarView = view.itemProfileHeaderAvatar
        val nameView: AppCompatTextView = view.itemProfileHeaderName
        val ageView: TextView = view.itemProfileHeaderAge
        val statusView: TextView = view.itemProfileHeaderStatus
    }

    class PostCreatorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.itemPostCreatorAvatar
        private val labelView: TextView = view.itemPostCreatorLabel
        val cameraBtn: AppCompatImageView = view.itemPostCreatorCameraBtn
        val titleView: TextInputEditText = view.itemPostCreatorTitle
        private val titleLayoutView: TextInputLayout = view.itemPostCreatorTitleLayout
        val textView: TextInputEditText = view.itemPostCreatorText
        val textLayoutView: TextInputLayout = view.itemPostCreatorTextLayout
        private val buttonsLayout: LinearLayout = view.itemPostCreatorButtonsLayout
        val cancelBtn: MaterialButton = view.itemPostCreatorCancelBtn
        val okBtn: MaterialButton = view.itemPostCreatorOkBtn
        fun expand() {
            labelView.visibility = View.GONE
            titleLayoutView.visibility = View.VISIBLE
            textLayoutView.visibility = View.VISIBLE
            buttonsLayout.visibility = View.VISIBLE
            view.isClickable = false
        }

        fun reduce() {
            labelView.visibility = View.VISIBLE
            titleLayoutView.visibility = View.GONE
            textLayoutView.visibility = View.GONE
            buttonsLayout.visibility = View.GONE
            view.isClickable = true
        }

        fun reset() {
            reduce()
            titleView.clearFocus()
            titleView.text = null
            textView.clearFocus()
            textView.text = null
            val inputMethodManager =
                itemView.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(itemView.windowToken, 0)
        }
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var invalidateLikes: () -> Unit = {}
        val avatarView: AvatarView = view.itemPostAvatar
        val nameView: AppCompatTextView = view.itemPostName
        val menuBtn: AppCompatImageView = view.itemPostMenuBtn
        val dateView: TextView = view.itemPostDate
        val titleView: TextView = view.itemPostTitle
        val textView: TextView = view.itemPostText
        val imagesViewGroup: LinearLayout = view.itemPostImagesHolder
        val likeBtn: AppCompatTextView = view.itemPostLikeBtn
        val commentBtn: AppCompatTextView = view.itemPostCommentBtn
    }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            FeedItemType.Header.viewType ->
                HeaderViewHolder(
                    layoutInflater.inflate(
                        R.layout.item_profile_header,
                        parent,
                        false
                    )
                )
            FeedItemType.PostCreator.viewType ->
                PostCreatorViewHolder(
                    layoutInflater.inflate(
                        R.layout.item_post_creator,
                        parent,
                        false
                    )
                )
            FeedItemType.Post.viewType ->
                PostViewHolder(
                    layoutInflater.inflate(
                        R.layout.item_post,
                        parent,
                        false
                    )
                )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context
        when (getItemViewType(position)) {
            FeedItemType.Header.viewType -> bindHeaderViewHolder(
                holder as HeaderViewHolder,
                position,
                context
            )
            FeedItemType.PostCreator.viewType -> {
                bindPostCreatorViewHolder(
                    holder as PostCreatorViewHolder,
                    position,
                    context
                )
            }
            FeedItemType.Post.viewType -> {
                bindPostViewHolder(
                    holder as PostViewHolder,
                    position,
                    context
                )
            }
        }
    }

    open fun bindHeaderViewHolder(headerViewHolder: HeaderViewHolder, position: Int, context: Context) {}

    open fun bindPostCreatorViewHolder(postCreatorViewHolder: PostCreatorViewHolder, position: Int, context: Context) {}

    open fun bindPostViewHolder(postViewHolder: PostViewHolder, position: Int, context: Context) {
        val post = feedItems[position] as PostItem

        Glide.with(context).load(post.avatar).into(postViewHolder.avatarView)
        postViewHolder.nameView.text = post.name
        postViewHolder.dateView.text = post.date

        postViewHolder.avatarView.setOnClickListener { feedItemsListener.openUserProfile(post.authorId) }
        postViewHolder.nameView.setOnClickListener { feedItemsListener.openUserProfile(post.authorId) }

        postViewHolder.menuBtn.apply {
            visibility = if (post.isPersonal) View.VISIBLE else View.GONE
            setOnClickListener {
                val postMenu = DialogFactory.createPostMenu(post.title, post.text,
                    {  // onDelete
                        feedItemsListener.deletePost(position, post.postId)
                    }, { title, text -> // onEdit
                        feedItemsListener.editPost(position, post.postId, title, text)
                    }
                )
//                currentBottomSheetDialog = postMenu
                feedItemsListener.showDialog(postMenu, "PostMenuBottomSheetDialog")
            }
        }

        if (post.title.isNullOrEmpty()) {
            postViewHolder.titleView.visibility = View.GONE
        } else {
            postViewHolder.titleView.visibility = View.VISIBLE
            postViewHolder.titleView.text = post.title
        }

        postViewHolder.imagesViewGroup.removeAllViews()
        val htmlUtils =
            HtmlParser(lifecycleOwner.lifecycleScope, context.resources, Glide.with(context))
        htmlUtils.provideFormattedText(
            post.text,
            weak(context),
            weak(postViewHolder.textView),
            weak(postViewHolder.imagesViewGroup)
        ) { onImageClick(it) }

        with(postViewHolder.likeBtn) {
            var hasPersonalLike: Boolean
            var likesCount: Int

            postViewHolder.invalidateLikes = {
                likesCount = post.likes.size
                hasPersonalLike =
                    post.likes.find { user -> user.id == PreferencesManager.userId } != null
                postViewHolder.likeBtn.text = if (likesCount == 0) null else likesCount.toString()
                val likeDrawable =
                    if (hasPersonalLike) R.drawable.ic_like_red
                    else R.drawable.ic_like_grey
                setCompoundDrawablesWithIntrinsicBounds(likeDrawable, 0, 0, 0)
            }

            setOnClickListener {
                hasPersonalLike =
                    post.likes.find { user -> user.id == PreferencesManager.userId } != null
                feedItemsListener.editLike(position, post.postId, !hasPersonalLike)
            }

            postViewHolder.invalidateLikes()
            setOnLongClickListener {
                val likesListFragment =
                    DialogFactory.createLikesListDialog(post.likes) { onUserClick(it) }
//                currentBottomSheetDialog = likesListFragment
                feedItemsListener.showDialog(likesListFragment, "LikesBottomSheetFragment")
                true
            }
        }

        val commentsCount = post.comments.size
        postViewHolder.commentBtn.text = if (commentsCount == 0) null else commentsCount.toString()
        postViewHolder.commentBtn.setOnClickListener {
            feedItemsListener.showCommentsList(post.postId, position)
//            val commentsListFragment =
//                DialogFactory.createCommentsListDialog(
//                    post.postId,
//                    post.comments,
//                    { onUserClick(it) },
//                    { onImageClick(it) },
//                    { text ->
//                        feedItemsListener.addComment(
//                            post.postId,
//                            position,
//                            text
//                        )
//                    },
//                    { commentPosition, commentId ->
//                        feedItemsListener.deleteComment(
//                            position,
//                            commentId,
//                            commentPosition
//                        )
//                    })
//            feedItemsListener.showDialog(commentsListFragment, "CommentsBottomSheetFragment")
        }
    }

    final override fun getItemViewType(position: Int) = feedItems[position].getType().viewType

    final override fun getItemCount() = feedItems.size

    final override fun getItemId(position: Int) = feedItems[position].hashCode().toLong()

    final override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    private fun onUserClick(userId: Int) {
        feedItemsListener.openUserProfile(userId)
    }

    private fun onImageClick(drawable: Drawable) {
        feedItemsListener.openImage(drawable)
    }
}