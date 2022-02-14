package ru.maxim.barybians.data.network.response

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    val id: Int,
    val text: String,
    @SerializedName("utime")
    val date: Long
)