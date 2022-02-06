package ru.maxim.barybians.ui.fragment.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ItemPostBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.feed.FeedRecyclerAdapter.PostViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.contains
import ru.maxim.barybians.utils.load
import javax.inject.Inject

class FeedRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : PagingDataAdapter<Post, PostViewHolder>(PostsDiffUtil) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
    }

    private var feedAdapterListener: FeedAdapterListener? = null

    fun setFeedItemsListener(listener: FeedAdapterListener?) {
        feedAdapterListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post?) = with(binding) {
            val context = itemView.context
            binding.post = post
            if (post == null) return@with
            binding.isPersonal = post.userId == preferencesManager.userId
            binding.hasPersonalLike = post.likedUsers.contains { it.id == preferencesManager.userId }

            itemPostAvatar.setOnClickListener { feedAdapterListener?.onProfileClick(post.userId) }
            itemPostName.setOnClickListener { feedAdapterListener?.onProfileClick(post.userId) }
            itemPostMenuBtn.setOnClickListener { feedAdapterListener?.onPostMenuClick(post.id) }

            val postBody = htmlUtils.parseHtml(post.text)
            itemPostText.text = postBody.first
            itemPostAttachmentsHolder.removeAllViews()
            postBody.second.forEach { attachment ->
                val imageView = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                        params.width = context.resources.getDimension(R.dimen.image_attachment_size).toInt()
                        params.height = params.width
                        params.marginEnd = context.resources.getDimension(R.dimen.attachment_space).toInt()
                        setOnClickListener { feedAdapterListener?.onImageClick(attachment.url) }
                    }
                }
                imageView.load(url = attachment.url)
                itemPostAttachmentsHolder.addView(imageView)
            }

            itemPostLikeBtn.setOnClickListener { feedAdapterListener?.onLikeClick(post.id) }
            itemPostLikeBtn.setOnLongClickListener { feedAdapterListener?.onLikeLongClick(post.id); true }
            itemPostCommentBtn.setOnClickListener { feedAdapterListener?.onCommentsClick(post.id) }
        }
    }

    private object PostsDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}