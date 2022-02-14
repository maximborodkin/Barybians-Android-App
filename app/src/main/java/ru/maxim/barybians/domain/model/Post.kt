package ru.maxim.barybians.domain.model

import java.util.*

data class Post(
    val postId: Int,
    val userId: Int,
    val title: String?,
    val text: String,
    val date: Date,
    val isEdited: Boolean,
    val author: User,
    val likedUsers: List<User>,
    val comments: List<Comment>
)