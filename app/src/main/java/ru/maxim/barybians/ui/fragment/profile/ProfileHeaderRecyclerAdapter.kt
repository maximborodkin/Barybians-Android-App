package ru.maxim.barybians.ui.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemProfileHeaderBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.profile.ProfileHeaderRecyclerAdapter.ProfileHeaderViewHolder
import javax.inject.Inject

class ProfileHeaderRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager
) :
    ListAdapter<User, ProfileHeaderViewHolder>(ProfileHeaderDiffUtil) {

    private var profileItemsListener: ProfileItemsListener? = null

    fun setProfileItemsListener(listener: ProfileItemsListener?) {
        profileItemsListener = listener
    }

    inner class ProfileHeaderViewHolder(private val binding: ItemProfileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {
            binding.user = user
            binding.isPersonal = user.userId == preferencesManager.userId
            binding.isDebug = preferencesManager.isDebug

            itemProfileHeaderBackButton.setOnClickListener { profileItemsListener?.onBackButtonClick() }
            itemProfileHeaderPreferencesButton.setOnClickListener { profileItemsListener?.onPreferencesButtonClick() }
            itemProfileHeaderStatus.setOnClickListener { profileItemsListener?.onStatusClick() }
            itemProfileHeaderChatButton.setOnClickListener { profileItemsListener?.onOpenChatButtonClick(user.userId) }
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

    private object ProfileHeaderDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = true

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }
}