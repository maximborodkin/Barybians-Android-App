package ru.maxim.barybians.ui.fragment.profile

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_post_creator.view.*
import kotlinx.android.synthetic.main.item_profile_header.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.DialogFactory
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.HtmlParser
import ru.maxim.barybians.utils.toast
import ru.maxim.barybians.utils.weak
import java.util.*


class ProfileRecyclerAdapter(private val profileItems: ArrayList<ProfileItem>,
                             private val profileItemsListener: ProfileItemsListener,
                             private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    OnUserClickListener,
    OnImageClickListener {

    private lateinit var recyclerView: RecyclerView
    var currentBottomSheetDialog: BottomSheetDialog? = null

    init {
        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.setItemViewCacheSize(20)
    }

    class ProfileHeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
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

    class ProfilePostCreatorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
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
            labelView.visibility = GONE
            titleLayoutView.visibility = VISIBLE
            textLayoutView.visibility = VISIBLE
            buttonsLayout.visibility = VISIBLE
            view.isClickable = false
        }
        fun reduce() {
            labelView.visibility = VISIBLE
            titleLayoutView.visibility = GONE
            textLayoutView.visibility = GONE
            buttonsLayout.visibility = GONE
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

    class ProfilePostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ProfileItemType.Header.viewType ->
                ProfileHeaderViewHolder(layoutInflater.inflate(R.layout.item_profile_header, parent, false))
            ProfileItemType.PostCreator.viewType ->
                ProfilePostCreatorViewHolder(layoutInflater.inflate(R.layout.item_post_creator, parent, false))
            ProfileItemType.Post.viewType ->
                ProfilePostViewHolder(layoutInflater.inflate(R.layout.item_post, parent, false))
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun getItemViewType(position: Int) = profileItems[position].getType().viewType

    override fun getItemCount() = profileItems.size

    override fun getItemId(position: Int) = profileItems[position].hashCode().toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context
        when(getItemViewType(position)) {
            ProfileItemType.Header.viewType -> {
                val headerViewHolder = holder as ProfileHeaderViewHolder
                if (headerViewHolder.isBinded) return
                val header = profileItems[position] as ProfileItemHeader

                if (header.isPersonal) {
                    headerViewHolder.backBtn.visibility = GONE
                    headerViewHolder.preferencesBtn.visibility = VISIBLE
                    headerViewHolder.preferencesBtn.setOnClickListener { profileItemsListener.openPreferences() }
                    headerViewHolder.editBtn.visibility = VISIBLE
                    headerViewHolder.editBtn.setOnClickListener { profileItemsListener.editUserInfo() }
                } else {
                    headerViewHolder.backBtn.visibility = VISIBLE
                    headerViewHolder.backBtn.setOnClickListener { profileItemsListener.popBackStack() }
                    headerViewHolder.preferencesBtn.visibility = GONE
                    headerViewHolder.editBtn.visibility = GONE
                }

                Glide.with(context).load(header.avatar).into(headerViewHolder.avatarView)
                Glide.with(context).load(header.avatar).into(headerViewHolder.backgroundView)

                headerViewHolder.nameView.apply {
                    text = header.name
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, header.roleDrawable, 0)
                    setOnTouchListener(View.OnTouchListener { view, event ->
                        view.performClick()
                        return@OnTouchListener if (event.action == MotionEvent.ACTION_DOWN &&
                            event.rawX >= right - compoundDrawables[2].bounds.width()
                        ) {
                            context.toast(context.getString(header.roleDescription))
                            true
                        } else false
                    })
                }

                val age = ((Date().time / 1000 - Date(header.birthDate).time) / (60 * 60 * 24 * 365)).toInt()
                headerViewHolder.ageView.text = context.resources.getQuantityString(R.plurals.age_plurals, age, age)

                if (!header.isPersonal && header.status.isNullOrBlank()) {
                    headerViewHolder.statusView.visibility = GONE
                } else if (header.isPersonal && header.status.isNullOrBlank()) {
                    headerViewHolder.statusView.text = context.getString(R.string.enter_your_status)
                    headerViewHolder.statusView
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit_grey, 0)
                } else {
                    headerViewHolder.statusView.text = header.status
                    headerViewHolder.statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }

                if (header.isPersonal) {
                    holder.statusView.setOnClickListener {
                        DialogFactory.createEditStatusDialog(context, header.status) {status ->
                            profileItemsListener.editStatus(status)
                        }.show()
                    }
                }

                headerViewHolder.isBinded = true
            }
            ProfileItemType.PostCreator.viewType -> {
                val postCreatorViewHolder = holder as ProfilePostCreatorViewHolder
                val postCreator = profileItems[position] as ProfileItemPostCreator

                Glide.with(context).load(postCreator.avatar).into(postCreatorViewHolder.avatarView)

                postCreatorViewHolder.cameraBtn.setOnClickListener {
                    // TODO("Make photo upload")
                    Toast.makeText(context, "Photo upload here", Toast.LENGTH_SHORT).show()
                }

                if (postCreator.isExpanded) postCreatorViewHolder.expand()
                else postCreatorViewHolder.reduce()

                postCreatorViewHolder.itemView.setOnClickListener {
                    if (!postCreator.isExpanded) {
                        postCreator.isExpanded = true
                        postCreatorViewHolder.expand()
                    }
                }
                postCreatorViewHolder.cancelBtn.setOnClickListener {
                    if (postCreator.isExpanded) {
                        postCreator.isExpanded = false
                        postCreatorViewHolder.reduce()
                    }
                }

                postCreatorViewHolder.textView.addTextChangedListener {
                    if (it.toString().isNotBlank()) { postCreatorViewHolder.textLayoutView.error = null }
                }

                postCreatorViewHolder.okBtn.setOnClickListener {
                    val title = postCreatorViewHolder.titleView.text.toString()
                    val text = postCreatorViewHolder.textView.text.toString()
                    if (text.isBlank()) {
                        postCreatorViewHolder.textLayoutView.error = context.getString(R.string.this_field_is_required)
                    } else {
                        postCreatorViewHolder.textLayoutView.error = null
                        profileItemsListener.addPost(title, text)
                    }
                }
            }
            ProfileItemType.Post.viewType -> {
                val postViewHolder = holder as ProfilePostViewHolder
                val post = profileItems[position] as ProfileItemPost

                Glide.with(context).load(post.avatar).into(postViewHolder.avatarView)
                postViewHolder.nameView.text = post.name
                postViewHolder.dateView.text = post.date

                postViewHolder.menuBtn.apply {
                    visibility = if (post.isPersonal) VISIBLE else GONE
                    setOnClickListener {
                        val postMenu = DialogFactory.createPostMenu(context, post.title, post.text,
                            {  // onDelete
                                profileItemsListener.deletePost(holder.adapterPosition, post.postId)
                            }, {title, text -> // onEdit
                                profileItemsListener.editPost(holder.adapterPosition, post.postId, title, text)
                            }
                        )
                        currentBottomSheetDialog = postMenu
                        postMenu.show()
                    }
                }

                if (post.title.isNullOrEmpty()) {
                    postViewHolder.titleView.visibility = GONE
                } else {
                    postViewHolder.titleView.visibility = VISIBLE
                    postViewHolder.titleView.text = post.title
                }

                postViewHolder.imagesViewGroup.removeAllViews()
                val htmlUtils = HtmlParser(lifecycleOwner.lifecycleScope, context.resources, Glide.with(context))
                htmlUtils.provideFormattedText(post.text, weak(context), weak(postViewHolder.textView), weak(postViewHolder.imagesViewGroup), this)

                with(postViewHolder.likeBtn) {
                    var hasPersonalLike: Boolean
                    var likesCount: Int

                    postViewHolder.invalidateLikes = {
                        likesCount = post.likes.size
                        hasPersonalLike = post.likes.find { user -> user.id == PreferencesManager.userId } != null
                        postViewHolder.likeBtn.text = if (likesCount == 0) null else likesCount.toString()
                        val likeDrawable =
                            if (hasPersonalLike) R.drawable.ic_like_red
                            else R.drawable.ic_like_grey
                        setCompoundDrawablesWithIntrinsicBounds(likeDrawable, 0, 0, 0)
                    }

                    setOnClickListener {
                        hasPersonalLike = post.likes.find { user -> user.id == PreferencesManager.userId } != null
                        profileItemsListener.editLike(holder.adapterPosition, post.postId, !hasPersonalLike)
                    }

                    postViewHolder.invalidateLikes()
                    setOnLongClickListener {
                        val likesListFragment =
                            DialogFactory.createLikesListDialog(context, post.likes, this@ProfileRecyclerAdapter)
                        currentBottomSheetDialog = likesListFragment
                        likesListFragment.show()
                        true
                    }
                }

                val commentsCount = post.comments.size
                postViewHolder.commentBtn.text = if (commentsCount == 0) null else commentsCount.toString()
                postViewHolder.commentBtn.setOnClickListener {
                    val commentsListFragment =
                        DialogFactory.createCommentsListDialog(context, post.comments, this, this, htmlUtils,
                            { text ->
                                profileItemsListener.addComment(post.postId, position, post.comments.size, text)
                            },
                            { commentsCount, commentPosition, commentId ->
                                profileItemsListener.deleteComment(position, commentsCount, commentId, commentPosition)
                            })
                    currentBottomSheetDialog = commentsListFragment
                    commentsListFragment.show()
                }

            }
            else -> return
        }
    }

    interface ProfileItemsListener {
        fun popBackStack()
        fun openPreferences() {}
        fun openUserProfile(userId: Int)
        fun openImage(drawable: Drawable)
        fun openDialog(userId: Int) {}
        fun editStatus(newStatus: String?) {}
        fun editUserInfo() {}
        fun addPost(title: String?, text: String)
        fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String)
        fun deletePost(itemPosition: Int, postId: Int)
        fun addComment(postId: Int, itemPosition: Int, commentsCount: Int, text: String)
        fun deleteComment(postPosition: Int, commentsCount: Int, commentId: Int, commentPosition: Int)
        fun editLike(itemPosition: Int, postId: Int, setLike: Boolean)
    }


    override fun onClick(userId: Int) {
        currentBottomSheetDialog?.dismiss()
        profileItemsListener.openUserProfile(userId)
    }

    override fun onImageClick(drawable: Drawable) {
        profileItemsListener.openImage(drawable)
    }
}

interface OnUserClickListener {
    fun onClick(userId: Int)
}

interface OnImageClickListener {
    fun onImageClick(drawable: Drawable)
}