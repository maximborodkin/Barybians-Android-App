package ru.maxim.barybians.data.repository

import ru.maxim.barybians.domain.model.Comment

interface CommentRepository {
    fun createComment(postId: Int, text: String): Comment
    fun editComment(commentId: Int, text: String): Comment
    fun deleteComment(commentId: Int): Boolean
}