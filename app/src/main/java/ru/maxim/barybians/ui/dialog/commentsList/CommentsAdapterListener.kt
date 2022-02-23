package ru.maxim.barybians.ui.dialog.commentsList

import android.view.View
import ru.maxim.barybians.domain.model.Comment

interface CommentsAdapterListener {
    fun onUserClick(userId: Int)
    fun onImageClick(url: String)
    fun onCommentMenuClick(comment: Comment, anchor: View)
}