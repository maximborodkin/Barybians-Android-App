package ru.maxim.barybians.domain.model

import java.util.*

data class Comment(
    val id: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    val date: Date,
    val author: User
)