package ru.maxim.barybians.ui.dialog.commentsList

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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemCommentBinding
import ru.maxim.barybians.domain.model.Attachment
import ru.maxim.barybians.domain.model.Attachment.AttachmentType.*
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.dialog.commentsList.CommentsListRecyclerAdapter.CommentViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.hide
import ru.maxim.barybians.utils.load
import ru.maxim.barybians.utils.show
import javax.inject.Inject

class CommentsListRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : ListAdapter<Comment, CommentViewHolder>(CommentDiffUtil) {

    private var commentsAdapterListener: CommentsAdapterListener? = null

    fun setAdapterListener(listener: CommentsAdapterListener?): CommentsListRecyclerAdapter {
        commentsAdapterListener = listener
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) = with(binding) {
            this.comment = comment
            this.isDebug = preferencesManager.isDebug

            // If there is no attachments, try to parse comment text as html
            if (comment.attachments.isEmpty()) {
                parseHtml(comment.text)
            } else {
                parseAttachments(comment.text, comment.attachments)
            }

            itemCommentUserAvatar.setOnClickListener { commentsAdapterListener?.onUserClick(comment.author.userId) }
            itemCommentUserName.setOnClickListener { commentsAdapterListener?.onUserClick(comment.author.userId) }
            itemCommentMenuButton.isVisible = preferencesManager.userId == comment.userId
            itemCommentMenuButton.setOnClickListener { button ->
                commentsAdapterListener?.onCommentMenuClick(comment, button)
            }
        }

        private fun parseHtml(rawText: String) = with(binding) {
            val context = itemView.context
            val commentBody = htmlUtils.parseHtml(rawText)
            itemCommentText.text = commentBody.first
            itemCommentAttachmentsHolder.removeAllViews()
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
                            setOnClickListener { commentsAdapterListener?.onImageClick(attachment.url) }
                        }
                    }
                }
                imageView.load(url = attachment.url)
                itemCommentAttachmentsHolder.addView(imageView)
            }
        }

        private fun parseAttachments(text: String, attachments: List<Attachment>) = with(binding) {
            val context = itemView.context
            val stickerAttachment = attachments.firstOrNull { attachment -> attachment.type == STICKER }
            if (stickerAttachment?.url != null && stickerAttachment.pack != null && stickerAttachment.sticker != null) {
                itemCommentText.hide()
                val stickerImageView = ImageView(context)
                stickerImageView.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                    params.width = context.resources.getDimension(R.dimen.sticker_size).toInt()
                    params.height = params.width
                }
                stickerImageView.load(url = stickerAttachment.url)
                itemCommentAttachmentsHolder.addView(stickerImageView)
            } else {
                itemCommentText.show()
                val spannableString = SpannableStringBuilder(text)
                itemCommentText.movementMethod = null
                itemCommentAttachmentsHolder.removeAllViews()

                for (attachment in attachments) {
                    when (attachment.type) {
                        STICKER -> {
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
                                setOnClickListener { commentsAdapterListener?.onImageClick(attachment.url) }
                                load(url = attachment.url)
                            }
                            itemCommentAttachmentsHolder.addView(imageView)
                            break // if message contains sticker, draw only it
                        }
                        STYLED -> {
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
                        LINK -> {
                            if (attachment.url != null) {
                                spannableString.setSpan(
                                    URLSpan(attachment.url),
                                    attachment.offset,
                                    attachment.offset + attachment.length,
                                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                )
                            }
                            itemCommentText.movementMethod = LinkMovementMethod.getInstance()
                        }
                        IMAGE -> {
                            if (attachment.url != null) {
                                val imageView = ImageView(context).apply {
                                    layoutParams =
                                        LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).also { params ->
                                            params.width = resources.getDimension(R.dimen.image_attachment_size).toInt()
                                            params.height = params.width
                                            params.marginEnd = resources.getDimension(R.dimen.attachment_space).toInt()
                                        }
                                    setOnClickListener { commentsAdapterListener?.onImageClick(attachment.url) }
                                    load(url = attachment.url)
                                }
                                itemCommentAttachmentsHolder.addView(imageView)
                            }
                        }
                        FILE -> {

                        }
                    }
                }
                itemCommentText.setText(spannableString, TextView.BufferType.SPANNABLE)
            }
        }
    }

    private object CommentDiffUtil : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.commentId == newItem.commentId

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem == newItem
    }
}