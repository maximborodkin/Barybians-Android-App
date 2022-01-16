package ru.maxim.barybians.ui.fragment.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.databinding.ItemUserBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.feed.LikesListRecyclerAdapter.UserViewHolder
import ru.maxim.barybians.utils.load
import java.util.*

class LikesListRecyclerAdapter(
    private val likes: List<User>,
    private val onUserClick: (userId: Int) -> Unit
) : ListAdapter<User, UserViewHolder>(UserDiffUtils) {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {
            itemUserAvatar.load(user.avatarMin)
            itemUserAvatar.isOnline = user.lastVisit > Date().time / 1000 - 5 * 60
            itemUserName.text = user.fullName
            root.setOnClickListener { onUserClick(user.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object UserDiffUtils : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem
    }
}