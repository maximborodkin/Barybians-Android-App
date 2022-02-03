package ru.maxim.barybians.data.repository

import ru.maxim.barybians.domain.model.Comment

interface CommentRepository {
    suspend fun createComment(uuid: String, postId: Int, text: String): Int
    suspend fun editComment(commentId: Int, text: String): Comment
    suspend fun deleteComment(commentId: Int): Boolean
    suspend fun getComments(postId: Int): List<Comment>?
}