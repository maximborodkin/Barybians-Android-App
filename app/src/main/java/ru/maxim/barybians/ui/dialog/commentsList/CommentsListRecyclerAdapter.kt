package ru.maxim.barybians.ui.dialog.commentsList

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ItemCommentBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.dialog.commentsList.CommentsListRecyclerAdapter.CommentViewHolder
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.SwipeDismissCallback
import ru.maxim.barybians.utils.load
import ru.maxim.barybians.utils.simpleDate
import javax.inject.Inject

class CommentsListRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val htmlUtils: HtmlUtils
) : ListAdapter<Comment, CommentViewHolder>(CommentDiffUtil) {

    private var commentsAdapterListener: CommentsAdapterListener? = null

    fun setAdapterListener(listener: CommentsAdapterListener?) {
        commentsAdapterListener = listener
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val swipeBackground = ColorDrawable(
            ContextCompat.getColor(recyclerView.context, R.color.delete_swipe_background)
        )
        val deleteIcon = requireNotNull(
            ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete_white)
        )

        val swipeDismissCallback = SwipeDismissCallback<CommentViewHolder>(
            swipeBackground = swipeBackground,
            iconDrawable = deleteIcon,
            allowSwipe = { viewHolder ->
                return@SwipeDismissCallback getItem(viewHolder.bindingAdapterPosition).author.id == preferencesManager.userId
            },
            onSwiped = { viewHolder ->
                val position = viewHolder.bindingAdapterPosition
                commentsAdapterListener?.onCommentSwipe(getItem(position).id, position)
            }
        )

        ItemTouchHelper(swipeDismissCallback).attachToRecyclerView(recyclerView)
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
            val context = itemView.context
            itemCommentUserAvatar.load(comment.author.avatarMin)
            itemCommentUserName.text = comment.author.fullName
            itemCommentDate.text = simpleDate(comment.date * 1000)

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

            itemCommentUserAvatar.setOnClickListener { commentsAdapterListener?.onUserClick(comment.author.id) }
            itemCommentUserName.setOnClickListener { commentsAdapterListener?.onUserClick(comment.author.id) }
            root.setOnLongClickListener {
                if (comment.author.id == preferencesManager.userId) {
                    commentsAdapterListener?.onCommentLongClick(comment.id, comment.text)
                }
                true
            }
        }
    }

    private object CommentDiffUtil : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem == newItem
    }
}
