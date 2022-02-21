package ru.maxim.barybians.domain.model

import java.util.*

data class Message(
    val messageId: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    val date: Date,
    val isUnread: Boolean,
    val attachments: List<Attachment>
)