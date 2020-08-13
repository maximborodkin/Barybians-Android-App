package ru.maxim.barybians.ui.fragment.feed

import android.graphics.drawable.Drawable
import androidx.fragment.app.DialogFragment

interface FeedItemsListener {
    fun openUserProfile(userId: Int)
    fun showDialog(dialogFragment: DialogFragment, tag: String)
    fun openImage(drawable: Drawable)
    fun editPost(itemPosition: Int, postId: Int, newTitle: String?, newText: String)
    fun deletePost(itemPosition: Int, postId: Int)
    fun addComment(postId: Int, itemPosition: Int, text: String)
    fun deleteComment(postPosition: Int, commentId: Int, commentPosition: Int)
    fun editLike(itemPosition: Int, postId: Int, setLike: Boolean)
}