package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class MessageDto(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    @SerializedName("utime")
    val _date: Long,
    val unread: Int,
    val attachments: List<AttachmentDto>?
)