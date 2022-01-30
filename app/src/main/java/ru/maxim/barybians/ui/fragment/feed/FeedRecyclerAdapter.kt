package ru.maxim.barybians.ui.fragment.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ItemPostBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.*

open class FeedRecyclerAdapter(
    private val currentUserId: Int,
    private val feedItemsListener: FeedItemsListener,
    private val dateFormatUtils: DateFormatUtils
) : ListAdapter<Post, FeedRecyclerAdapter.PostViewHolder>(PostsDiffUtil) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setItemViewCacheSize(20)
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = with(binding) {
            post.author.let {
                itemPostAvatar.apply {
                    load(post.author.avatarMin)
                    itemPostAvatar.setOnClickListener { feedItemsListener.onProfileClick(post.author.id) }
                }

                itemPostName.apply {
                    text = post.author.fullName
                    itemPostName.setOnClickListener { feedItemsListener.onProfileClick(post.author.id) }
                }
            }

            itemPostDate.text = dateFormatUtils.getSimplifiedDate(post.date)

            itemPostMenuBtn.apply {
                isVisible = post.userId == currentUserId
                setOnClickListener { feedItemsListener.onPostMenuClick(post.id) }
            }

            itemPostTitle.apply {
                text = post.title
                isVisible = post.title.isNotNullOrBlank()
            }

            itemPostImagesHolder.removeAllViews()
//            val htmlUtils =
//                HtmlParser(lifecycleOwner.lifecycleScope, context.resources, Glide.with(context))
//            htmlUtils.provideFormattedText(
//                post.text,
//                weak(context),
//                weak(postViewHolder.text),
//                weak(postViewHolder.imagesViewGroup)
//            ) { onImageClick(it) }

            itemPostLikeBtn.apply {
                val hasPersonalLike = post.likedUsers.find { it.id == currentUserId }.isNotNull()
                text = if (post.likedUsers.isEmpty()) null else post.likedUsers.size.toString()
                val likeDrawable =
                    if (hasPersonalLike) R.drawable.ic_like_red
                    else R.drawable.ic_like_grey
                setDrawableStart(likeDrawable)
                setOnClickListener { feedItemsListener.onLikeClick(post.id, hasPersonalLike) }
                setOnLongClickListener {
                    feedItemsListener.onLikeLongClick(post.id)
                    return@setOnLongClickListener true
                }
            }

            with(itemPostCommentBtn) {
                text = if (post.comments.isEmpty()) null else post.comments.size.toString()
                setOnClickListener { feedItemsListener.onCommentsClick(post.id) }
            }
        }
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

//    override fun getItemId(position: Int) = getItem(position).id.toLong()

    object PostsDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}