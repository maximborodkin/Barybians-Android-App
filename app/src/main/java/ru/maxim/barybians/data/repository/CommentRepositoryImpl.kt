package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.service.CommentService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Comment
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService,
    private val preferencesManager: PreferencesManager,
    private val repositoryBound: RepositoryBound
): CommentRepository {

    override fun createComment(postId: Int, text: String): Comment {
        TODO("Not yet implemented")
    }

    override fun editComment(commentId: Int, text: String): Comment {
        TODO("Not yet implemented")
    }

    override fun deleteComment(commentId: Int): Boolean {
        TODO("Not yet implemented")
    }

}