package ru.maxim.barybians.ui.fragment.profile

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemProfileHeaderBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.profile.ProfileHeaderRecyclerAdapter.ProfileHeaderViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.hide
import ru.maxim.barybians.utils.show
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class ProfileHeaderRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : ListAdapter<User?, ProfileHeaderViewHolder>(ProfileHeaderDiffUtil) {

    private var profileItemsListener: ProfileItemsListener? = null

    fun setProfileItemsListener(listener: ProfileItemsListener?) {
        profileItemsListener = listener
    }

    inner class ProfileHeaderViewHolder(private val binding: ItemProfileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User?) = with(binding) {
            if (user != null) {
                binding.user = user
                binding.isPersonal = user.userId == preferencesManager.userId
                binding.isDebug = preferencesManager.isDebug

                itemProfileHeaderProgressBar.hide()
                itemProfileHeaderAge.show()
                itemProfileHeaderAvatar.setOnClickListener { profileItemsListener?.onImageClick(user.avatarFull) }
                itemProfileHeaderPreferencesButton.setOnClickListener { profileItemsListener?.onPreferencesButtonClick() }
                itemProfileHeaderChatButton.setOnClickListener { profileItemsListener?.onOpenChatButtonClick(user.userId) }
                itemProfileHeaderName.setOnClickListener { itemView.context.toast(user.role.stringResource) }
                itemProfileHeaderStatus.text = htmlUtils.parseHtml(user.status ?: String()).first
                if (preferencesManager.userId == user.userId) {
                    itemProfileHeaderStatus.setOnClickListener { profileItemsListener?.onStatusClick() }
                }
            } else {
                binding.isPersonal = true
                val placeholderAvatar = ColorDrawable(itemProfileHeaderCardBackground.solidColor)
                itemProfileHeaderProgressBar.show()
                itemProfileHeaderBackground.setImageDrawable(null)
                itemProfileHeaderAvatar.setImageDrawable(placeholderAvatar)
                itemProfileHeaderAvatar.isOnline = false
                itemProfileHeaderName.text = null
                itemProfileHeaderAge.hide()
                itemProfileHeaderStatus.text = null
                itemProfileHeaderChatButton.hide()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHeaderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProfileHeaderBinding.inflate(layoutInflater, parent, false)
        return ProfileHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileHeaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object ProfileHeaderDiffUtil : DiffUtil.ItemCallback<User?>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = true

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }
}