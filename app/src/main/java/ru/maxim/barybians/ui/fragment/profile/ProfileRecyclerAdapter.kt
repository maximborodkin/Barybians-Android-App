package ru.maxim.barybians.ui.fragment.profile

import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.DialogFactory
import ru.maxim.barybians.ui.base.*
import ru.maxim.barybians.utils.toast
import java.util.*
import kotlin.collections.ArrayList

class ProfileRecyclerAdapter(
    private val feedItems: ArrayList<FeedItem>,
    private val profileItemsListener: ProfileItemsListener,
    private val lifecycleOwner: LifecycleOwner
) : FeedRecyclerAdapter(feedItems, profileItemsListener, lifecycleOwner) {

    override fun bindHeaderViewHolder(headerViewHolder: HeaderViewHolder, position: Int) {
        super.bindHeaderViewHolder(headerViewHolder, position)
        val context = headerViewHolder.itemView.context
        if (headerViewHolder.isBinded) return
        val header = feedItems[position] as HeaderItem

        if (header.isPersonal) {
            headerViewHolder.backBtn.visibility = View.GONE
            headerViewHolder.preferencesBtn.visibility = View.VISIBLE
            headerViewHolder.preferencesBtn.setOnClickListener { profileItemsListener.openPreferences() }
            headerViewHolder.editBtn.visibility = View.VISIBLE
            headerViewHolder.editBtn.setOnClickListener { profileItemsListener.editUserInfo() }
        } else {
            headerViewHolder.backBtn.visibility = View.VISIBLE
            headerViewHolder.backBtn.setOnClickListener { profileItemsListener.popBackStack() }
            headerViewHolder.preferencesBtn.visibility = View.GONE
            headerViewHolder.editBtn.visibility = View.GONE
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
                    context.toast(header.roleDescription)
                    true
                } else false
            })
        }

        val age = ((Date().time / 1000 - Date(header.birthDate).time) / (60 * 60 * 24 * 365)).toInt()
        headerViewHolder.ageView.text = context.resources.getQuantityString(R.plurals.age_plurals, age, age)

        if (!header.isPersonal && header.status.isNullOrBlank()) {
            headerViewHolder.statusView.visibility = View.GONE
        } else if (header.isPersonal && header.status.isNullOrBlank()) {
            headerViewHolder.statusView.text = context.getString(R.string.enter_your_status)
            headerViewHolder.statusView
                .setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_edit_grey, 0)
        } else {
            headerViewHolder.statusView.text = header.status
            headerViewHolder.statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }

        if (header.isPersonal) {
            headerViewHolder.statusView.setOnClickListener {
                DialogFactory.createEditStatusDialog(context, header.status) { status ->
                    profileItemsListener.editStatus(status)
                }.show()
            }
        }

        headerViewHolder.isBinded = true
    }

    override fun bindPostCreatorViewHolder(postCreatorViewHolder: PostCreatorViewHolder, position: Int) {
        super.bindPostCreatorViewHolder(postCreatorViewHolder, position)
        val context = postCreatorViewHolder.itemView.context
        val postCreator = feedItems[position] as PostCreatorItem

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
}