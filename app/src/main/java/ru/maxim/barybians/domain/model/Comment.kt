package ru.maxim.barybians.domain.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Comment (
    @PrimaryKey
    val id: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    @SerializedName("utime")
    val date: Long,
    val author: User
)