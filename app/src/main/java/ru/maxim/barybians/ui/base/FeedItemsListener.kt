package ru.maxim.barybians.ui.base

import android.graphics.drawable.Drawable

interface FeedItemsListener {
    fun openUserProfile(userId: Int)
    fun openImage(drawable: Drawable)
    fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String)
    fun deletePost(itemPosition: Int, postId: Int)
    fun addComment(postId: Int, itemPosition: Int, commentsCount: Int, text: String)
    fun deleteComment(postPosition: Int, commentsCount: Int, commentId: Int, commentPosition: Int)
    fun editLike(itemPosition: Int, postId: Int, setLike: Boolean)
}