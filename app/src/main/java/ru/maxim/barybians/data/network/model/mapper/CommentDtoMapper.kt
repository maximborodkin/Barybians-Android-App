package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.network.model.CommentDto
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Comment
import java.util.*
import javax.inject.Inject

class CommentDtoMapper @Inject constructor(
    private val attachmentDtoMapper: AttachmentDtoMapper,
    private val userDtoMapper: UserDtoMapper
) : DomainMapper<CommentDto, Comment>() {

    override fun toDomainModel(model: CommentDto): Comment =
        Comment(
            commentId = model.commentId,
            postId = model.postId,
            userId = model.userId,
            text = model.text,
            date = Date(model.date * 1000),
            attachments = attachmentDtoMapper.toDomainModelList(model.attachments ?: emptyList()),
            author = userDtoMapper.toDomainModel(model.author)
        )

    override fun fromDomainModel(domainModel: Comment): CommentDto =
        CommentDto(
            commentId = domainModel.commentId,
            postId = domainModel.postId,
            userId = domainModel.userId,
            text = domainModel.text,
            date = domainModel.date.time / 1000,
            attachments = attachmentDtoMapper.fromDomainModelList(domainModel.attachments),
            author = userDtoMapper.fromDomainModel(domainModel.author)
        )
}