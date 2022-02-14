package ru.maxim.barybians.data.repository

import androidx.paging.PagingSource
import ru.maxim.barybians.data.RepositoryBound
import ru.maxim.barybians.data.database.dao.CommentDao
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.mapper.CommentEntityMapper
import ru.maxim.barybians.data.network.model.mapper.CommentDtoMapper
import ru.maxim.barybians.data.network.service.CommentService
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.domain.model.Comment
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService,
    private val commentDao: CommentDao,
    private val commentEntityMapper: CommentEntityMapper,
    private val commentDtoMapper: CommentDtoMapper,
    private val postService: PostService,
    private val repositoryBound: RepositoryBound
) : CommentRepository {

    // TODO: Temporary method. In future it will be replaced by
    //  commentService().loadCommentsPage(postId: Int, startIndex: Int, count: Int)
    override suspend fun loadCommentsPage(postId: Int, startIndex: Int, count: Int): List<Comment> {
        val commentDto = repositoryBound.wrapRequest { postService.getById(postId) }?.comments ?: emptyList()
        return commentDtoMapper.toDomainModelList(commentDto)
    }

    override fun commentsPagingSource(postId: Int): PagingSource<Int, CommentEntity> {
        return commentDao.pagingSource(postId)
    }

    override suspend fun createComment(uuid: String, postId: Int, text: String) {
        val commentDto = repositoryBound.wrapRequest { commentService.addComment(uuid, postId, text) }
        val comment = commentDtoMapper.toDomainModel(commentDto)
        commentDao.insert(commentEntityMapper.fromDomainModel(comment))
    }

    override suspend fun editComment(commentId: Int, text: String) {
        val commentDto = repositoryBound.wrapRequest { commentService.editComment(commentId, text) }
        val comment = commentDtoMapper.toDomainModel(commentDto)
        commentDao.insert(commentEntityMapper.fromDomainModel(comment))
    }

    override suspend fun deleteComment(commentId: Int) {
        val isDeleted = repositoryBound.wrapRequest { commentService.deleteComment(commentId) }
        if (isDeleted) {
            commentDao.delete(commentId)
        }
    }
}