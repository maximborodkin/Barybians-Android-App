package ru.maxim.barybians.ui.dialog.commentsList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ItemCommentBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.dialog.commentsList.CommentsListRecyclerAdapter.CommentViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.load
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
            val context = itemView.context
            val commentBody = htmlUtils.parseHtml(comment.text)
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

            itemCommentUserAvatar.setOnClickListener { commentsAdapterListener?.onUserClick(comment.author.userId) }
            itemCommentUserName.setOnClickListener { commentsAdapterListener?.onUserClick(comment.author.userId) }
            itemCommentMenuButton.isVisible = preferencesManager.userId == comment.userId
            itemCommentMenuButton.setOnClickListener { button ->
                commentsAdapterListener?.onCommentMenuClick(comment, button)
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
