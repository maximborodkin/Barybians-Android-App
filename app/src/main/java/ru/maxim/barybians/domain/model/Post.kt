package ru.maxim.barybians.domain.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val userId: Int,
    val title: String?,
    val text: String,
    @SerializedName("utime")
    val _date: Long,
    val edited: Int,
    val author: User,
    val likedUsers: List<User>,
    val comments: List<Comment>
) {
    val date: Long
        get() = _date * 1000
}