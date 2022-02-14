package ru.maxim.barybians.ui.dialog.likesList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.databinding.ItemUserBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.dialog.likesList.LikesListRecyclerAdapter.UserViewHolder
import ru.maxim.barybians.utils.load
import java.util.*
import javax.inject.Inject

class LikesListRecyclerAdapter @Inject constructor() :
    ListAdapter<User, UserViewHolder>(UserDiffUtils) {

    private var onUserClick: ((userId: Int) -> Unit)? = null

    fun setOnUserClickListener(listener: ((userId: Int) -> Unit)?) {
        onUserClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {
            itemUserAvatar.load(user.avatarMin)
            itemUserAvatar.isOnline = user.isOnline
            itemUserName.text = user.fullName
            root.setOnClickListener { onUserClick?.invoke(user.userId) }
        }
    }

    private object UserDiffUtils : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.userId == newItem.userId

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem
    }
}