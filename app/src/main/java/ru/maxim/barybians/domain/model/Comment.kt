package ru.maxim.barybians.domain.model

import java.util.*

data class Comment(
    val commentId: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    val date: Date,
    val attachments: List<Attachment>,
    val author: User
)