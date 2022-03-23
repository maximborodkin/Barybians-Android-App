package ru.maxim.barybians.data.repository.comment

import androidx.lifecycle.LiveData
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.domain.model.Comment

interface CommentRepository {
    suspend fun createComment(uuid: String, postId: Int, text: String, parseMode: ParseMode = ParseMode.MD)
    suspend fun editComment(commentId: Int, text: String, parseMode: ParseMode = ParseMode.MD)
    suspend fun deleteComment(commentId: Int)
    fun getComments(postId: Int, sortingDirection: Boolean): LiveData<List<Comment>>
}