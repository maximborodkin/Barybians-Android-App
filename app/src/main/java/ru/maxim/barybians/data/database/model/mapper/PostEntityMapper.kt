package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Post
import java.util.*
import javax.inject.Inject

class PostEntityMapper @Inject constructor(
    private val userEntityMapper: UserEntityMapper,
    private val commentEntityMapper: CommentEntityMapper,
    private val attachmentEntityMapper: AttachmentEntityMapper
) : DomainMapper<PostEntity, Post>() {

    override fun toDomainModel(model: PostEntity): Post =
        Post(
            postId = model.post.postId,
            userId = model.post.userId,
            title = model.post.title,
            text = model.post.text,
            date = Date(model.post.date),
            isEdited = model.post.edited == 1,
            attachments = attachmentEntityMapper.toDomainModelList(model.attachments),
            author = userEntityMapper.toDomainModel(model.author),
            likedUsers = userEntityMapper.toDomainModelList(model.likes),
            comments = commentEntityMapper.toDomainModelList(model.comments)
        )

    /*
    * The game is over
    * No more rounds to play, it's time to pay
    * Who's got the Joker?
    * To kill the lies and make your hurt recall
    * There is no other sky to fall
    * */
    override fun fromDomainModel(domainModel: Post): PostEntity =
        PostEntity(
            post = PostEntity.PostEntityBody(
                postId = domainModel.postId,
                userId = domainModel.userId,
                title = domainModel.title,
                text = domainModel.text,
                date = domainModel.date.time,
                edited = if (domainModel.isEdited) 1 else 0,
            ),
            attachments = attachmentEntityMapper.fromDomainModelList(domainModel.attachments),
            author = userEntityMapper.fromDomainModel(domainModel.author),
            likes = userEntityMapper.fromDomainModelList(domainModel.likedUsers),
            comments = commentEntityMapper.fromDomainModelList(domainModel.comments)
        )
}