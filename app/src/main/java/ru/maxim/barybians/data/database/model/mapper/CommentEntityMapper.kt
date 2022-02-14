package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.domain.model.Comment
import java.util.*
import javax.inject.Inject

class CommentEntityMapper @Inject constructor(
    private val userDao: UserDao,
    private val userEntityMapper: UserEntityMapper
) : DomainMapper<CommentEntity, Comment>() {

    override suspend fun toDomainModel(model: CommentEntity): Comment {
        return Comment(
            commentId = model.commentId,
            postId = model.postId,
            userId = model.userId,
            text = model.text,
            date = Date(model.date),
            author = userEntityMapper.toDomainModel(model.author)
        )
    }

    override suspend fun fromDomainModel(domainModel: Comment): CommentEntity {
        return CommentEntity(
            commentId = domainModel.commentId,
            postId = domainModel.postId,
            userId = domainModel.userId,
            text = domainModel.text,
            date = domainModel.date.time,
            author = userEntityMapper.fromDomainModel(domainModel.author)
        )
    }
}