package ru.maxim.barybians.ui.fragment.feed

import ru.maxim.barybians.domain.model.Post

interface FeedAdapterListener {
    fun onProfileClick(userId: Int)
    fun onImageClick(imageUrl: String)
    fun onPostMenuClick(post: Post)
    fun onCommentsClick(postId: Int)
    fun onLikeClick(postId: Int)
    fun onLikeLongClick(postId: Int)
}