package ru.maxim.barybians.data.persistence.database.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.persistence.database.dao.UserDao
import ru.maxim.barybians.data.persistence.database.model.CommentEntity
import ru.maxim.barybians.domain.model.Comment
import javax.inject.Inject

class CommentEntityMapper @Inject constructor(
    private val userDao: UserDao,
    private val userEntityMapper: UserEntityMapper
) : DomainMapper<CommentEntity, Comment>() {

    override suspend fun toDomainModel(model: CommentEntity): Comment {
        val author = requireNotNull(userDao.getById(model.userId))

        return Comment(
            id = model.commentId,
            postId = model.postId,
            userId = model.userId,
            text = model.text,
            _date = model.date,
            author = userEntityMapper.toDomainModel(author)
        )
    }

    override suspend fun fromDomainModel(domainModel: Comment): CommentEntity {
        userDao.insert(userEntityMapper.fromDomainModel(domainModel.author))

        return CommentEntity(
            commentId = domainModel.id,
            postId = domainModel.postId,
            userId = domainModel.userId,
            text = domainModel.text,
            date = domainModel._date
        )
    }
}