package ru.maxim.barybians.ui.dialog.commentsList

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ItemCommentBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.dialog.commentsList.CommentsRecyclerAdapter.CommentViewHolder
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.SwipeDismissCallback
import ru.maxim.barybians.utils.load
import javax.inject.Inject

class CommentsRecyclerAdapter @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val dateFormatUtils: DateFormatUtils,
) : ListAdapter<Comment, CommentViewHolder>(CommentDiffUtil) {

    private var onUserClick: ((userId: Int) -> Unit)? = null
    private var onImageClick: ((drawable: Drawable) -> Unit)? = null
    private var onCommentSwipe: ((commentId: Int, viewHolderPosition: Int) -> Unit)? = null
    private var onCommentLongClick: ((commentId: Int, commentText: String) -> Unit)? = null

    fun setOnUserClickListener(listener: ((userId: Int) -> Unit)?) {
        onUserClick = listener
    }

    fun setOnImageClickListener(listener: ((drawable: Drawable) -> Unit)?) {
        onImageClick = listener
    }

    fun setOnCommentSwipeListener(listener: ((commentId: Int, viewHolderPosition: Int) -> Unit)?) {
        onCommentSwipe = listener
    }

    fun setOnCommentLongClickListener(listener: ((commentId: Int, commentText: String) -> Unit)?) {
        onCommentLongClick = listener
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
                onCommentSwipe?.invoke(getItem(position).id, position)
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

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) = with(binding) {
            itemCommentUserAvatar.load(comment.author.avatarMin)
            itemCommentUserName.text = comment.author.fullName
            itemCommentDate.text = dateFormatUtils.getSimplifiedDate(comment.date * 1000)

            itemCommentText.text =
                HtmlCompat.fromHtml(comment.text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            itemCommentAttachmentsHolder.removeAllViews()
            // TODO: place attachments in holder

            itemCommentUserAvatar.setOnClickListener { onUserClick?.invoke(comment.author.id) }
            itemCommentUserName.setOnClickListener { onUserClick?.invoke(comment.author.id) }
            root.setOnLongClickListener {
                if (comment.author.id == preferencesManager.userId) {
                    onCommentLongClick?.invoke(comment.id, comment.text)
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
