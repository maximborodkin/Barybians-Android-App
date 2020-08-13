package ru.maxim.barybians.ui.fragment.profile

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_comment.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.fragment.base.PostItem
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.HtmlParser
import ru.maxim.barybians.utils.weak

class CommentsRecyclerAdapter(private val comments: ArrayList<PostItem.CommentItem>,
                              private val onUserClick: (userId: Int) -> Unit,
                              private val onImageClick: (drawable: Drawable) -> Unit,
                              private val deleteCommentCallback: (commentPosition: Int,
                                                                  commentId: Int) -> Unit,
                              private val htmlParser: HtmlParser
) : RecyclerView.Adapter<CommentsRecyclerAdapter.CommentViewHolder>() {

    private lateinit var swipeBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable

    private val itemTouchHelperCallback = object : SimpleCallback(0, LEFT or RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            deleteCommentCallback(
                viewHolder.adapterPosition,
                comments[viewHolder.adapterPosition].id
            )
        }

        override fun getSwipeDirs(recyclerView: RecyclerView,
                                  viewHolder: RecyclerView.ViewHolder
        ): Int {
            Log.d("getSwipeDirs", "adapterPosition: ${viewHolder.adapterPosition}, commentId: ${comments[viewHolder.adapterPosition].id}")
            val authorId = comments[viewHolder.adapterPosition].author.id
            val isPersonal = authorId == PreferencesManager.userId
            return if(isPersonal) Callback.makeMovementFlags(0, LEFT or RIGHT) else 0
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
            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight)/2

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
            }else{
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
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
        swipeBackground =
            ColorDrawable(ContextCompat.getColor(recyclerView.context, R.color.delete_swipe_background))
        deleteIcon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete_white)!!
    }

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarView: AvatarView = view.itemCommentUserAvatar
        val nameView: TextView = view.itemCommentUserName
        val dateView: TextView = view.itemCommentDate
        val textView: TextView = view.itemCommentText
        val imagesHolder: LinearLayout = view.itemCommentImagesHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        Glide.with(holder.itemView.context).load(comment.author.avatar).into(holder.avatarView)
        holder.nameView.text = comment.author.name
        holder.avatarView.setOnClickListener {
            onUserClick(comment.author.id)
        }
        holder.nameView.setOnClickListener {
            onUserClick(comment.author.id)
        }
        holder.dateView.text = comment.date
        holder.textView.text = null
        holder.imagesHolder.removeAllViews()
        htmlParser.provideFormattedText(
            comment.text,
            weak(holder.itemView.context),
            weak(holder.textView),
            weak(holder.imagesHolder),
            onImageClick
        )
    }
}
