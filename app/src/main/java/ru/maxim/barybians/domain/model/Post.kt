package ru.maxim.barybians.domain.model

import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.UserEntity
import java.util.*

data class Post(
    val id: Int,
    val userId: Int,
    val title: String?,
    val text: String,
    val date: Date,
    val edited: Boolean,
    val author: UserEntity,
    val likedUsers: List<UserEntity>,
    val comments: List<CommentEntity>
)