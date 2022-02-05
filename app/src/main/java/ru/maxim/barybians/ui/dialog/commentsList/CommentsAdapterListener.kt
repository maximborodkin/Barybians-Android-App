package ru.maxim.barybians.ui.dialog.commentsList

interface CommentsAdapterListener {
    fun onUserClick(userId: Int)
    fun onImageClick(url: String)
    fun onCommentSwipe(commentId: Int, viewHolderPosition: Int)
    fun onCommentLongClick(commentId: Int, commentText: String)
}