package ru.maxim.barybians.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Post (
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val author: User?,
    val title: String?,
    val text: String,
    @SerializedName("utime")
    val date: Long,
    val likedUsers: ArrayList<User>,
    val likesCount: Int,
    val comments: ArrayList<Comment>
)