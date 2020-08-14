package ru.maxim.barybians.ui.fragment.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.fragment.base.PostItem
import ru.maxim.barybians.ui.fragment.profile.LikedUsersRecyclerAdapter.UserViewHolder
import ru.maxim.barybians.ui.view.AvatarView

class LikedUsersRecyclerAdapter(private val users: ArrayList<PostItem.UserItem>,
                                private val onUserClick: (userId: Int) -> Unit
) : RecyclerView.Adapter<UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.itemUserAvatar
        val nameView: TextView = view.itemUserName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        Glide.with(holder.itemView.context).load(user.avatar).into(holder.avatarView)
        holder.nameView.text = user.name
        holder.itemView.setOnClickListener { onUserClick(user.id) }
    }
}