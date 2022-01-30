package ru.maxim.barybians.ui.fragment.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ItemPostBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.contains
import javax.inject.Inject

open class FeedRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : ListAdapter<Post, FeedRecyclerAdapter.PostViewHolder>(PostsDiffUtil) {

    private var feedItemsListener: FeedItemsListener? = null

    fun setFeedItemsListener(listener: FeedItemsListener?) {
        feedItemsListener = listener
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = with(binding) {
            val hasPersonalLike = post.likedUsers.contains { it.id == preferencesManager.userId }
            binding.post = post
            binding.isPersonal = post.userId == preferencesManager.userId
            binding.hasPersonalLike = hasPersonalLike

            itemPostAvatar.setOnClickListener { feedItemsListener?.onProfileClick(post.userId) }
            itemPostName.setOnClickListener { feedItemsListener?.onProfileClick(post.userId) }
            itemPostMenuBtn.setOnClickListener { feedItemsListener?.onPostMenuClick(post.id) }
            itemPostText.text = htmlUtils.createSpannableString(post.text)

            itemPostAttachmentsHolder.removeAllViews()

            itemPostLikeBtn.setOnClickListener { feedItemsListener?.onLikeClick(post.id) }
            itemPostLikeBtn.setOnLongClickListener { feedItemsListener?.onLikeLongClick(post.id); true }

            itemPostCommentBtn.setOnClickListener { feedItemsListener?.onCommentsClick(post.id) }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setItemViewCacheSize(20)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object PostsDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}