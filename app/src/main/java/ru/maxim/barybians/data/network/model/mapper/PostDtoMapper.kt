package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.network.model.PostDto
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Post
import java.util.*
import javax.inject.Inject

class PostDtoMapper @Inject constructor(
    private val userDtoMapper: UserDtoMapper,
    private val commentDtoMapper: CommentDtoMapper
) : DomainMapper<PostDto, Post>() {

    override fun toDomainModel(model: PostDto): Post =
        Post(
            postId = model.postId,
            userId = model.userId,
            title = model.title,
            text = model.text,
            date = Date(model.date * 1000),
            isEdited = model.edited == 1,
            author = userDtoMapper.toDomainModel(model.author),
            likedUsers = userDtoMapper.toDomainModelList(model.likedUsers),
            comments = commentDtoMapper.toDomainModelList(model.comments)
        )

    override fun fromDomainModel(domainModel: Post): PostDto =
        PostDto(
            postId = domainModel.postId,
            userId = domainModel.userId,
            title = domainModel.title,
            text = domainModel.text,
            date = domainModel.date.time / 1000,
            edited = if (domainModel.isEdited) 1 else 0,
            author = userDtoMapper.fromDomainModel(domainModel.author),
            likedUsers = userDtoMapper.fromDomainModelList(domainModel.likedUsers),
            comments = commentDtoMapper.fromDomainModelList(domainModel.comments),
            attachments = listOf() // TODO: attachments
        )
}