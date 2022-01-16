package ru.maxim.barybians.ui.fragment.feed

import android.graphics.drawable.Drawable
import androidx.fragment.app.DialogFragment

interface FeedItemsListener {
    fun onProfileClick(userId: Int)
    fun onImageClick(drawable: Drawable)
    fun onImageClick(imageUrl: String)
    fun onPostMenuClick(postId: Int)
    fun onCommentsClick(postId: Int)
    fun onLikeClick(postId: Int, hasPersonalLike: Boolean)
    fun onLikeLongClick(postId: Int)
}