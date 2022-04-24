package ru.maxim.barybians.domain.model

import java.util.Date

data class Message(
    val messageId: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    val date: Date,
    val attachments: List<Attachment>,
    val status: MessageStatus
) {

    enum class MessageStatus(val id: Int) {
        Sending(0),
        Unread(1),
        Read(2),
        Error(3)
    }
}