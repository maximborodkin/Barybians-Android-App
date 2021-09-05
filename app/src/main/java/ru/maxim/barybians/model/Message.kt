package ru.maxim.barybians.model

import com.google.gson.annotations.SerializedName

data class Message (
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    @SerializedName("utime")
    val time: Long,
    val unread: Int,
    val attachments: ArrayList<MessageAttachment>?
)