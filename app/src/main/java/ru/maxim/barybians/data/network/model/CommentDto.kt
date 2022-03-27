package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id")
    val commentId: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    @SerializedName("utime")
    val date: Long,
    val attachments: List<AttachmentDto>?, // TODO: remove ?, when the field in server response be not null
    val author: UserDto
)