package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class CommentDto(
    val commentId: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    @SerializedName("utime")
    val date: Long,
    val author: UserDto
)