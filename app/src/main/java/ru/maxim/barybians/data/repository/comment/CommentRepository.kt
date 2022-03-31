package ru.maxim.barybians.data.repository.comment

import androidx.lifecycle.LiveData
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.domain.model.Comment

interface CommentRepository {
    suspend fun createComment(parseMode: ParseMode, uuid: String, postId: Int, text: String)
    suspend fun editComment(parseMode: ParseMode, commentId: Int, text: String)
    suspend fun deleteComment(commentId: Int)
    fun getComments(postId: Int, sortingDirection: Boolean): LiveData<List<Comment>>
}