package ru.maxim.barybians.ui.fragment.feed

interface FeedAdapterListener {
    fun onProfileClick(userId: Int)
    fun onImageClick(imageUrl: String)
    fun onPostMenuClick(postId: Int)
    fun onCommentsClick(postId: Int)
    fun onLikeClick(postId: Int)
    fun onLikeLongClick(postId: Int)
}