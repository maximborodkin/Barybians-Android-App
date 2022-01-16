package ru.maxim.barybians.ui.fragment.feed

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ItemCommentBinding
import ru.maxim.barybians.domain.model.Comment
import ru.maxim.barybians.ui.fragment.feed.CommentsRecyclerAdapter.CommentViewHolder
import ru.maxim.barybians.utils.DateFormatUtils
import ru.maxim.barybians.utils.load

class CommentsRecyclerAdapter(
    private val currentUserId: Int,
    private val onUserClick: (userId: Int) -> Unit,
    private val onImageClick: (drawable: Drawable) -> Unit,
    private val onCommentSwipe: (commentId: Int) -> Unit,
//    private val htmlParser: HtmlParser,
    private val dateFormatUtils: DateFormatUtils
) : ListAdapter<Comment, CommentViewHolder>(CommentDiffUtil) {

    private lateinit var swipeBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable

    private val swipeToDismissCallback = object : SimpleCallback(0, LEFT or RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            onCommentSwipe(getItem(viewHolder.bindingAdapterPosition).id)
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Allow to swipe ony personal comments
            val authorId = getItem(viewHolder.bindingAdapterPosition).author.id
            val isPersonal = authorId == currentUserId
            return if (isPersonal) Callback.makeMovementFlags(0, LEFT or RIGHT) else 0
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

            if (dX > 0) {
                swipeBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    dX.toInt(),
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.left + iconMargin,
                    itemView.top + iconMargin,
                    itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMargin
                )
            } else {
                swipeBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                deleteIcon.setBounds(
                    itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                    itemView.top + iconMargin,
                    itemView.right - iconMargin,
                    itemView.bottom - iconMargin
                )
            }
            swipeBackground.draw(c)
            deleteIcon.draw(c)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        ItemTouchHelper(swipeToDismissCallback).attachToRecyclerView(recyclerView)
        swipeBackground =
            ColorDrawable(
                ContextCompat.getColor(
                    recyclerView.context,
                    R.color.delete_swipe_background
                )
            )
        deleteIcon = requireNotNull(
            ContextCompat.getDrawable(
                recyclerView.context,
                R.drawable.ic_delete_white
            )
        )
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) = with(binding) {
            itemCommentUserAvatar.load(comment.author.avatarMin)
            itemCommentUserName.text = comment.author.fullName
            itemCommentDate.text = dateFormatUtils.getSimplifiedDate(comment.date * 1000)

            itemCommentText.text = null
            itemCommentAttachmentsHolder.removeAllViews()
            // TODO: get Spannable string from HtmlParser and set text and attachments

            itemCommentUserAvatar.setOnClickListener { onUserClick(comment.author.id) }
            itemCommentUserName.setOnClickListener { onUserClick(comment.author.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object CommentDiffUtil : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem == newItem
    }
}
