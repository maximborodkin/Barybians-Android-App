package ru.maxim.barybians.ui.fragment.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ItemPostBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.feed.FeedRecyclerAdapter.PostViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.contains
import javax.inject.Inject

class FeedRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : PagingDataAdapter<Post, PostViewHolder>(PostsDiffUtil) {

    private var feedItemsListener: FeedItemsListener? = null

    fun setFeedItemsListener(listener: FeedItemsListener?) {
        feedItemsListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post?) = with(binding) {
            binding.post = post
            if (post == null) return@with
            val hasPersonalLike = post.likedUsers.contains { it.id == preferencesManager.userId }
            binding.isPersonal = post.userId == preferencesManager.userId
            binding.hasPersonalLike = hasPersonalLike

            itemPostAvatar.setOnClickListener { feedItemsListener?.onProfileClick(post.userId) }
            itemPostName.setOnClickListener { feedItemsListener?.onProfileClick(post.userId) }
            itemPostMenuBtn.setOnClickListener { feedItemsListener?.onPostMenuClick(post.id) }
            itemPostText.text = htmlUtils.parseHtml(post.text).first

            itemPostAttachmentsHolder.removeAllViews()

            itemPostLikeBtn.setOnClickListener { feedItemsListener?.onLikeClick(post.id) }
            itemPostLikeBtn.setOnLongClickListener { feedItemsListener?.onLikeLongClick(post.id); true }

            itemPostCommentBtn.setOnClickListener { feedItemsListener?.onCommentsClick(post.id) }
        }
    }

    private object PostsDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}