package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Comment
import java.util.*
import javax.inject.Inject

class CommentEntityMapper @Inject constructor(
    private val attachmentEntityMapper: AttachmentEntityMapper,
    private val userEntityMapper: UserEntityMapper
) : DomainMapper<CommentEntity, Comment>() {

    override fun toDomainModel(model: CommentEntity): Comment =
        Comment(
            commentId = model.comment.commentId,
            postId = model.comment.postId,
            userId = model.comment.userId,
            text = model.comment.text,
            date = Date(model.comment.date),
            attachments = attachmentEntityMapper.toDomainModelList(model.attachments),
            author = userEntityMapper.toDomainModel(model.author)
        )


    override fun fromDomainModel(domainModel: Comment): CommentEntity =
        CommentEntity(
            comment = CommentEntity.CommentEntityBody(
                commentId = domainModel.commentId,
                postId = domainModel.postId,
                userId = domainModel.userId,
                text = domainModel.text,
                date = domainModel.date.time
            ),
            attachments = attachmentEntityMapper.fromDomainModelList(domainModel.attachments),
            author = userEntityMapper.fromDomainModel(domainModel.author)
        )
}