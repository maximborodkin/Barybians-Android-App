package ru.maxim.barybians.data.repository

import androidx.paging.PagingSource
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.domain.model.Comment

interface CommentRepository {
    fun commentsPagingSource(postId: Int): PagingSource<Int, CommentEntity>
    suspend fun loadCommentsPage(postId: Int, startIndex: Int, count: Int): List<Comment>
    suspend fun createComment(uuid: String, postId: Int, text: String)
    suspend fun editComment(commentId: Int, text: String)
    suspend fun deleteComment(commentId: Int)

    companion object {
        const val pageSize = 30
        const val prefetchDistance = 5
    }
}