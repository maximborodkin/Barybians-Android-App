package ru.maxim.barybians.ui.fragment.profile

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.item_post_creator.view.*
import kotlinx.android.synthetic.main.item_profile_header.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.fragment.base.FeedItem
import ru.maxim.barybians.ui.fragment.base.HeaderItem
import ru.maxim.barybians.ui.fragment.base.PostCreatorItem
import ru.maxim.barybians.ui.fragment.feed.FeedRecyclerAdapter
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.*
import java.util.*

class ProfileRecyclerAdapter(
    private val feedItems: List<FeedItem>,
    private val profileItemsListener: ProfileItemsListener,
    currentUserId: Int,
    lifecycleOwner: LifecycleOwner)
// : FeedRecyclerAdapter(feedItems, currentUserId, profileItemsListener, lifecycleOwner) {
//
//    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        var isBinded = false
//        val backgroundView: ImageView = view.itemProfileHeaderImageBackground
//        val backBtn: AppCompatImageView = view.itemProfileHeaderBack
//        val preferencesBtn: AppCompatImageView = view.itemProfileHeaderPreferences
//        val editBtn: AppCompatImageView = view.itemProfileHeaderEdit
//        val avatarView: AvatarView = view.itemProfileHeaderAvatar
//        val nameView: AppCompatTextView = view.itemProfileHeaderName
//        val ageView: TextView = view.itemProfileHeaderAge
//        val statusView: TextView = view.itemProfileHeaderStatus
//        val openChatBtn: MaterialButton = view.itemProfileHeaderDialogBtn
//    }
//
//    class PostCreatorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val avatarView: AvatarView = view.itemPostCreatorAvatar
//        private val labelView: TextView = view.itemPostCreatorLabel
//        val cameraBtn: AppCompatImageView = view.itemPostCreatorCameraBtn
//        val titleView: TextInputEditText = view.itemPostCreatorTitle
//        private val titleLayoutView: TextInputLayout = view.itemPostCreatorTitleLayout
//        val textView: TextInputEditText = view.itemPostCreatorText
//        val textLayoutView: TextInputLayout = view.itemPostCreatorTextLayout
//        private val buttonsLayout: LinearLayout = view.itemPostCreatorButtonsLayout
//        val cancelBtn: MaterialButton = view.itemPostCreatorCancelBtn
//        val okBtn: MaterialButton = view.itemPostCreatorOkBtn
//        fun expand() {
//            labelView.visibility = View.GONE
//            titleLayoutView.visibility = View.VISIBLE
//            textLayoutView.visibility = View.VISIBLE
//            buttonsLayout.visibility = View.VISIBLE
//            view.isClickable = false
//        }
//
//        fun reduce() {
//            labelView.visibility = View.VISIBLE
//            titleLayoutView.visibility = View.GONE
//            textLayoutView.visibility = View.GONE
//            buttonsLayout.visibility = View.GONE
//            view.isClickable = true
//        }
//
//        fun reset() {
//            reduce()
//            titleView.clearFocus()
//            titleView.text = null
//            textView.clearFocus()
//            textView.text = null
//            val inputMethodManager =
//                itemView.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(itemView.windowToken, 0)
//        }
//    }
//
//    override fun bindHeaderViewHolder(
//        headerViewHolder: HeaderViewHolder,
//        position: Int,
//        context: Context
//    ) {
//        super.bindHeaderViewHolder(headerViewHolder, position, context)
//        if (headerViewHolder.isBinded) return
//        val header = feedItems[position] as HeaderItem
//
//        if (header.isPersonal) {
//            headerViewHolder.backBtn.visibility = GONE
//            headerViewHolder.openChatBtn.visibility = GONE
//            headerViewHolder.preferencesBtn.visibility = VISIBLE
//            headerViewHolder.preferencesBtn.setOnClickListener { profileItemsListener.openPreferences() }
//            headerViewHolder.editBtn.visibility = VISIBLE
//            headerViewHolder.editBtn.setOnClickListener { profileItemsListener.editUserInfo() }
//        } else {
//            headerViewHolder.backBtn.apply {
//                visibility = VISIBLE
//                setOnClickListener { /*profileItemsListener.popBackStack()*/ }
//            }
//            headerViewHolder.openChatBtn.apply {
//                visibility = VISIBLE
//                setOnClickListener {
//                    profileItemsListener.openDialog(
//                        header.userId,
//                        header.avatarSmall,
//                        header.name
//                    )
//                }
//            }
//            headerViewHolder.preferencesBtn.visibility = GONE
//            headerViewHolder.editBtn.visibility = GONE
//        }
//
//        Glide.with(context).load(header.avatarSmall).into(headerViewHolder.avatarView)
//        headerViewHolder.avatarView.setOnClickListener {
//            if (!header.avatarFull.isNullOrBlank())
//                onImageClick(header.avatarFull)
//        }
//        Glide.with(context).load(header.avatarSmall).into(headerViewHolder.backgroundView)
//
//        headerViewHolder.nameView.apply {
//            text = header.name
//            setCompoundDrawablesWithIntrinsicBounds(0, 0, header.roleDrawable, 0)
//            setOnTouchListener(View.OnTouchListener { view, event ->
//                view.performClick()
//                return@OnTouchListener if (event.action == MotionEvent.ACTION_DOWN &&
//                    event.rawX >= right - compoundDrawables[2].bounds.width()
//                ) {
//                    context.toast(header.roleDescription)
//                    true
//                } else false
//            })
//        }
//
//        val age =
//            ((Date().time / 1000 - Date(header.birthDate).time) / (60 * 60 * 24 * 365)).toInt()
//        headerViewHolder.ageView.text =
//            context.resources.getQuantityString(R.plurals.age_plurals, age, age)
//
//        if (!header.isPersonal && header.status.isNullOrBlank()) {
//            headerViewHolder.statusView.visibility = GONE
//        } else if (header.isPersonal && header.status.isNullOrBlank()) {
//            headerViewHolder.statusView.text = context.getString(R.string.enter_your_status)
//            headerViewHolder.statusView.setDrawableEnd(R.drawable.ic_edit_grey)
//        } else {
//            headerViewHolder.statusView.text = header.status
//            headerViewHolder.statusView.clearDrawables()
//        }
//
//        if (header.isPersonal) {
//            headerViewHolder.statusView.setOnClickListener {
//                profileItemsListener.showEditStatusDialog(header.status)
//            }
//        }
//
//        headerViewHolder.isBinded = true
//    }
//
//    override fun bindPostCreatorViewHolder(
//        postCreatorViewHolder: PostCreatorViewHolder,
//        position: Int,
//        context: Context
//    ) {
//        super.bindPostCreatorViewHolder(postCreatorViewHolder, position, context)
//        val postCreator = feedItems[position] as PostCreatorItem
//
//        Glide.with(context).load(postCreator.avatar).into(postCreatorViewHolder.avatarView)
//
//        postCreatorViewHolder.cameraBtn.setOnClickListener {
//            // TODO("Make photo upload")
//            Toast.makeText(context, "Photo upload here", Toast.LENGTH_SHORT).show()
//        }
//
//        if (postCreator.isExpanded) postCreatorViewHolder.expand()
//        else postCreatorViewHolder.reduce()
//
//        postCreatorViewHolder.itemView.setOnClickListener {
//            if (!postCreator.isExpanded) {
//                postCreator.isExpanded = true
//                postCreatorViewHolder.expand()
//            }
//        }
//        postCreatorViewHolder.cancelBtn.setOnClickListener {
//            if (postCreator.isExpanded) {
//                postCreator.isExpanded = false
//                postCreatorViewHolder.reduce()
//            }
//        }
//
//        postCreatorViewHolder.textView.addTextChangedListener {
//            if (it.toString().isNotBlank()) {
//                postCreatorViewHolder.textLayoutView.error = null
//            }
//        }
//
//        postCreatorViewHolder.okBtn.setOnClickListener {
//            val title = postCreatorViewHolder.titleView.text.toString()
//            val text = postCreatorViewHolder.textView.text.toString()
//            if (text.isBlank()) {
//                postCreatorViewHolder.textLayoutView.error =
//                    context.getString(R.string.this_field_is_required)
//            } else {
//                postCreatorViewHolder.textLayoutView.error = null
//                profileItemsListener.addPost(title, text)
//            }
//        }
//    }
//}