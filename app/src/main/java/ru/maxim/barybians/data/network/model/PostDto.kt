package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id")
    val postId: Int,
    val userId: Int,
    val title: String?,
    val text: String,
    @SerializedName("utime")
    val date: Long,
    val edited: Int,
    val attachments: List<AttachmentDto>?, // TODO: remove, when the field in server response be not null
    val author: UserDto,
    val likedUsers: List<UserDto>,
    val comments: List<CommentDto>
)