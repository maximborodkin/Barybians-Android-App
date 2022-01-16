package ru.maxim.barybians.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Post (
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val author: User?,
    var title: String?,
    var text: String,
    @SerializedName("utime")
    var date: Long,
    val likedUsers: ArrayList<User>,
    var likesCount: Int,
    val comments: ArrayList<Comment>
)