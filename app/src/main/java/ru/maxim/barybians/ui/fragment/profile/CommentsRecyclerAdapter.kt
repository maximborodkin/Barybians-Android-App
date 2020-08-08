package ru.maxim.barybians.ui.fragment.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_comment.view.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.view.AvatarView
import ru.maxim.barybians.utils.HtmlUtils
import ru.maxim.barybians.utils.weak

class CommentsRecyclerAdapter(private val comments: ArrayList<ProfileItemPost.ItemComment>,
                              private val onUserClickListener: OnUserClickListener,
                              private val onImageClickListener: OnImageClickListener,
                              private val htmlUtils: HtmlUtils
) : RecyclerView.Adapter<CommentsRecyclerAdapter.CommentViewHolder>() {

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
        holder.avatarView.setOnClickListener { onUserClickListener.onClick(comment.author.id) }
        holder.nameView.setOnClickListener { onUserClickListener.onClick(comment.author.id) }
        holder.dateView.text = comment.date
        holder.textView.text = null
        holder.imagesHolder.removeAllViews()
        htmlUtils.loadPost(comment.text, weak(holder.itemView.context),
            weak(holder.textView), weak(holder.imagesHolder), onImageClickListener)
    }
}