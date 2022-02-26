package ru.maxim.barybians.ui.fragment.postsList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemPostBinding
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.postsList.PostsListRecyclerAdapter.PostViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.contains
import ru.maxim.barybians.utils.load
import javax.inject.Inject

class PostsListRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : PagingDataAdapter<Post, PostViewHolder>(PostsDiffUtil) {

    private var postsListAdapterListener: PostsListAdapterListener? = null

    fun setAdapterListener(listener: PostsListAdapterListener?) {
        postsListAdapterListener = listener
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
        recyclerView.scrollBarSize =
            if (preferencesManager.isDebug) recyclerView.context.resources.getDimensionPixelSize(R.dimen.scrollbar_size)
            else 0
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
            binding.post = post ?: return@with
            val context = itemView.context
            isDebug = preferencesManager.isDebug
            isPersonal = post.userId == preferencesManager.userId
            hasPersonalLike = post.likedUsers.contains { it.userId == preferencesManager.userId }

            itemPostAvatar.setOnClickListener { postsListAdapterListener?.onProfileClick(post.userId) }
            itemPostName.setOnClickListener { postsListAdapterListener?.onProfileClick(post.userId) }
            itemPostMenuBtn.setOnClickListener { button ->
                postsListAdapterListener?.onPostMenuClick(
                    post = getItem(bindingAdapterPosition) ?: return@setOnClickListener,
                    anchor = button
                )
            }

            val postBody = htmlUtils.parseHtml(post.text)
            itemPostText.text = postBody.first
            itemPostAttachmentsHolder.removeAllViews()
            postBody.second.forEach { attachment ->
                val imageView = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                        params.width = context.resources.getDimension(R.dimen.image_attachment_size).toInt()
                        params.height = params.width
                        params.marginEnd = context.resources.getDimension(R.dimen.attachment_space).toInt()
                        setOnClickListener { postsListAdapterListener?.onImageClick(attachment.url) }
                    }
                }
                imageView.load(url = attachment.url)
                itemPostAttachmentsHolder.addView(imageView)
            }

            itemPostLikeBtn.setOnClickListener { postsListAdapterListener?.onLikeClick(post.postId) }
            itemPostLikeBtn.setOnLongClickListener { postsListAdapterListener?.onLikeLongClick(post.postId); true }
            itemPostCommentBtn.setOnClickListener { postsListAdapterListener?.onCommentsClick(post.postId) }
        }
    }

    private object PostsDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.postId == newItem.postId
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}