package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class PostDto(
    val id: Int,
    val userId: Int,
    val title: String?,
    val text: String,
    @SerializedName("utime")
    val _date: Long,
    val edited: Int,
    val author: UserDto,
    val likedUsers: List<UserDto>,
    val comments: List<CommentDto>
)