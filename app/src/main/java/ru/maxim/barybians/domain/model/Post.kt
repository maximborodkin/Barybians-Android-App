package ru.maxim.barybians.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Post (
    @PrimaryKey
    val id: Int,
    val userId: Int,
    var title: String?,
    var text: String,
    @SerializedName("utime")
    var date: Long,
    val edited: Int,
    val author: User,
    var likedUsers: List<User>,
    var comments: ArrayList<Comment>
)