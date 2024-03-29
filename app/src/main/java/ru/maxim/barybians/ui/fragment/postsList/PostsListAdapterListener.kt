package ru.maxim.barybians.ui.fragment.postsList

import android.view.View
import ru.maxim.barybians.domain.model.Post

interface PostsListAdapterListener {
    fun onProfileClick(userId: Int)
    fun onImageClick(imageUrl: String)
    fun onPostMenuClick(post: Post, anchor: View)
    fun onCommentsClick(postId: Int)
    fun onLikeClick(postId: Int)
    fun onLikeLongClick(postId: Int)
}