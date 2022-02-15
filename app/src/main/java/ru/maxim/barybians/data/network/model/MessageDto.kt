package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("id")
    val messageId: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    @SerializedName("utime")
    val date: Long,
    val unread: Int,
    val attachments: List<AttachmentDto>? = listOf()
)