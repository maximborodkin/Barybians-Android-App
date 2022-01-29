package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.service.CommentService
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Comment
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService,
    private val postService: PostService,
    private val preferencesManager: PreferencesManager,
    private val repositoryBound: RepositoryBound
) : CommentRepository {

    override suspend fun getComments(postId: Int): List<Comment> =
        repositoryBound.wrapRequest { postService.getById(postId) }.find { it.id == postId }!!.comments

    override suspend fun createComment(uuid: String, postId: Int, text: String): Int =
        repositoryBound.wrapRequest { commentService.addComment(uuid, postId, text) }

    override suspend fun editComment(commentId: Int, text: String): Comment =
        repositoryBound.wrapRequest { commentService.editComment(commentId, text) }

    override suspend fun deleteComment(commentId: Int): Boolean =
        repositoryBound.wrapRequest { commentService.deleteComment(commentId) }
}