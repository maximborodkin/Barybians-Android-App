package ru.maxim.barybians.ui.fragment.postsList

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemPostBinding
import ru.maxim.barybians.domain.model.Attachment
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.ui.fragment.postsList.PostsListRecyclerAdapter.PostViewHolder
import ru.maxim.barybians.utils.*
import timber.log.Timber
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
        recyclerView.itemAnimator = null // Disable blinking on data updates
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
            binding.isDebug = preferencesManager.isDebug
            binding.isPersonal = post.userId == preferencesManager.userId
            binding.hasPersonalLike = post.likedUsers.contains { like -> like.userId == preferencesManager.userId }
            Timber.d("XXX ${post.postId} - $hasPersonalLike")
            itemPostTitle.isVisible = post.title.isNotNullOrBlank()

            // If there is no attachments, try to parse comment text as html
            if (post.attachments.isEmpty()) {
                parseHtml(post.text)
            } else {
                parseAttachments(post.text, post.attachments)
            }

            itemPostAvatar.setOnClickListener { postsListAdapterListener?.onProfileClick(post.userId) }
            itemPostName.setOnClickListener { postsListAdapterListener?.onProfileClick(post.userId) }
            itemPostMenuBtn.setOnClickListener { button ->
                postsListAdapterListener?.onPostMenuClick(
                    post = getItem(bindingAdapterPosition) ?: return@setOnClickListener,
                    anchor = button
                )
            }
            itemPostLikeBtn.setOnClickListener { postsListAdapterListener?.onLikeClick(post.postId) }
            itemPostLikeBtn.setOnLongClickListener { postsListAdapterListener?.onLikeLongClick(post.postId); true }
            itemPostCommentBtn.setOnClickListener { postsListAdapterListener?.onCommentsClick(post.postId) }
        }

        private fun parseHtml(rawText: String) = with(binding) {
            val context = itemView.context
            val commentBody = htmlUtils.parseHtml(rawText)
            itemPostText.text = commentBody.first
            itemPostAttachmentsHolder.removeAllViews()
            commentBody.second.forEach { attachment ->
                val imageView = ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                        if (attachment.isSticker) {
                            params.width = context.resources.getDimension(R.dimen.sticker_size).toInt()
                            params.height = params.width
                        } else {
                            params.width = context.resources.getDimension(R.dimen.image_attachment_size).toInt()
                            params.height = params.width
                            params.marginEnd = context.resources.getDimension(R.dimen.attachment_space).toInt()
                            setOnClickListener { postsListAdapterListener?.onImageClick(attachment.url) }
                        }
                    }
                }
                imageView.load(url = attachment.url)
                itemPostAttachmentsHolder.addView(imageView)
            }
        }

        private fun parseAttachments(text: String, attachments: List<Attachment>) = with(binding) {
            val context = itemView.context
            val stickerAttachment = attachments.firstOrNull { attachment -> attachment.type == Attachment.AttachmentType.STICKER }
            if (stickerAttachment?.url != null && stickerAttachment.pack != null && stickerAttachment.sticker != null) {
                itemPostTitle.hide()
                itemPostText.hide()
                val stickerImageView = ImageView(context)
                stickerImageView.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                    params.width = context.resources.getDimension(R.dimen.sticker_size).toInt()
                    params.height = params.width
                }
                stickerImageView.load(url = stickerAttachment.url)
                itemPostAttachmentsHolder.addView(stickerImageView)
            } else {
                itemPostText.isVisible = text.isNotBlank()
                val spannableString = SpannableStringBuilder(text)
                itemPostText.movementMethod = null
                itemPostAttachmentsHolder.removeAllViews()

                for (attachment in attachments) {
                    when (attachment.type) {
                        Attachment.AttachmentType.STICKER -> {
                            if (attachment.url.isNullOrBlank() ||
                                attachment.pack.isNullOrBlank() ||
                                attachment.sticker == null || attachment.sticker <= 0
                            ) continue

                            val imageView = ImageView(context).apply {
                                layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                                    params.width = resources.getDimension(R.dimen.image_attachment_size).toInt()
                                    params.height = params.width
                                    params.marginEnd = resources.getDimension(R.dimen.attachment_space).toInt()
                                }
                                setOnClickListener { postsListAdapterListener?.onImageClick(attachment.url) }
                                load(url = attachment.url)
                            }
                            itemPostAttachmentsHolder.addView(imageView)
                            break // if message contains sticker, draw only it
                        }
                        Attachment.AttachmentType.STYLED -> {
                            if (attachment.style != null) {
                                when (attachment.style) {
                                    Attachment.StyledAttachmentType.BOLD -> {
                                        spannableString.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            attachment.offset,
                                            attachment.offset + attachment.length,
                                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                        )
                                    }
                                    Attachment.StyledAttachmentType.ITALIC ->
                                        spannableString.setSpan(
                                            StyleSpan(Typeface.ITALIC),
                                            attachment.offset,
                                            attachment.offset + attachment.length,
                                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                        )
                                    Attachment.StyledAttachmentType.UNDERLINE -> {
                                        spannableString.setSpan(
                                            UnderlineSpan(),
                                            attachment.offset,
                                            attachment.offset + attachment.length,
                                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                        )
                                    }
                                    Attachment.StyledAttachmentType.STRIKE -> {
                                        spannableString.setSpan(
                                            StrikethroughSpan(),
                                            attachment.offset,
                                            attachment.offset + attachment.length,
                                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                        )
                                    }
                                }
                            }
                        }
                        Attachment.AttachmentType.LINK -> {
                            if (attachment.url != null) {
                                spannableString.setSpan(
                                    URLSpan(attachment.url),
                                    attachment.offset,
                                    attachment.offset + attachment.length,
                                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                )
                            }
                            itemPostText.movementMethod = LinkMovementMethod.getInstance()
                        }
                        Attachment.AttachmentType.IMAGE -> {
                            if (attachment.url != null) {
                                val imageView = ImageView(context).apply {
                                    layoutParams =
                                        LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                                            params.width = resources.getDimension(R.dimen.image_attachment_size).toInt()
                                            params.height = params.width
                                            params.marginEnd = resources.getDimension(R.dimen.attachment_space).toInt()
                                        }
                                    setOnClickListener { postsListAdapterListener?.onImageClick(attachment.url) }
                                    load(url = attachment.url)
                                }
                                itemPostAttachmentsHolder.addView(imageView)
                            }
                        }
                        Attachment.AttachmentType.FILE -> {

                        }
                    }
                }
                itemPostText.setText(spannableString, TextView.BufferType.SPANNABLE)
            }
        }
    }

    private object PostsDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.postId == newItem.postId
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}