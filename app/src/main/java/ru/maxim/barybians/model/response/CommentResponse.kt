package ru.maxim.barybians.model.response

import com.google.gson.annotations.SerializedName

data class CommentResponse (
    val id: Int,
    val text: String,
    @SerializedName("utime")
    val date: Long
)