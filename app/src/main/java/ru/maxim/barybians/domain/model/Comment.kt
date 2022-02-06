package ru.maxim.barybians.domain.model

import com.google.gson.annotations.SerializedName

data class Comment(
    val id: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    @SerializedName("utime")
    val _date: Long,
    val author: User
) {
    val date: Long
        get() = _date * 1000
}