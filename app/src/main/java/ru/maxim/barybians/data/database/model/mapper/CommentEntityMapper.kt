package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Comment
import java.util.*
import javax.inject.Inject

class CommentEntityMapper @Inject constructor(
    private val userEntityMapper: UserEntityMapper
) : DomainMapper<CommentEntity, Comment>() {

    override suspend fun toDomainModel(model: CommentEntity): Comment {
        return Comment(
            commentId = model.comment.commentId,
            postId = model.comment.postId,
            userId = model.comment.userId,
            text = model.comment.text,
            date = Date(model.comment.date),
            author = userEntityMapper.toDomainModel(model.author)
        )
    }

    override suspend fun fromDomainModel(domainModel: Comment): CommentEntity {
        return CommentEntity(
            comment = CommentEntity.CommentEntityBody(
                commentId = domainModel.commentId,
                postId = domainModel.postId,
                userId = domainModel.userId,
                text = domainModel.text,
                date = domainModel.date.time
            ),
            author = userEntityMapper.fromDomainModel(domainModel.author)
        )
    }
}