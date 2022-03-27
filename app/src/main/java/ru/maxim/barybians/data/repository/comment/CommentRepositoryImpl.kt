package ru.maxim.barybians.data.repository.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.dao.CommentAttachmentDao
import ru.maxim.barybians.data.database.dao.CommentDao
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.mapper.CommentEntityMapper
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.data.network.model.mapper.CommentDtoMapper
import ru.maxim.barybians.data.network.service.CommentService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.Comment
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentService: CommentService,
    private val attachmentDao: AttachmentDao,
    private val commentAttachmentDao: CommentAttachmentDao,
    private val commentDao: CommentDao,
    private val userDao: UserDao,
    private val commentEntityMapper: CommentEntityMapper,
    private val commentDtoMapper: CommentDtoMapper,
    private val repositoryBound: RepositoryBound
) : CommentRepository {

    override fun getComments(postId: Int, sortingDirection: Boolean): LiveData<List<Comment>> {
        return commentDao.getByPostId(postId, sortingDirection)
            .map { commentsList -> commentEntityMapper.toDomainModelList(commentsList) }
    }

    override suspend fun createComment(uuid: String, postId: Int, text: String, parseMode: ParseMode) = withContext(IO) {
        val commentDto = repositoryBound.wrapRequest {
            commentService.addComment(
                uuid = uuid,
                postId = postId,
                text = text,
                parseMode = parseMode.headerValue
            )
        }
        val comment = commentDtoMapper.toDomainModel(commentDto)
        commentDao.save(commentEntityMapper.fromDomainModel(comment), attachmentDao, commentAttachmentDao, userDao)
    }

    override suspend fun editComment(commentId: Int, text: String, parseMode: ParseMode) = withContext(IO) {
        val commentDto = repositoryBound.wrapRequest {
            commentService.editComment(
                commentId = commentId,
                text = text,
                parseMode = parseMode.headerValue
            )
        }
        val comment = commentDtoMapper.toDomainModel(commentDto)
        commentDao.save(commentEntityMapper.fromDomainModel(comment), attachmentDao, commentAttachmentDao, userDao)
    }

    override suspend fun deleteComment(commentId: Int) = withContext(IO) {
        val isDeleted = repositoryBound.wrapRequest { commentService.deleteComment(commentId) }
        if (isDeleted) {
            commentDao.delete(commentId)
        }
    }
}