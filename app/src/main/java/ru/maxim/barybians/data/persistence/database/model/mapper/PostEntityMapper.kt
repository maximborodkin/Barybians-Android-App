package ru.maxim.barybians.data.persistence.database.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.persistence.database.dao.CommentDao
import ru.maxim.barybians.data.persistence.database.dao.LikeDao
import ru.maxim.barybians.data.persistence.database.dao.UserDao
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.domain.model.Post
import javax.inject.Inject

class PostEntityMapper @Inject constructor(
    private val userDao: UserDao,
    private val likeDao: LikeDao,
    private val commentDao: CommentDao,
    private val userEntityMapper: UserEntityMapper,
    private val commentEntityMapper: CommentEntityMapper
) : DomainMapper<PostEntity, Post>() {

    override suspend fun toDomainModel(model: PostEntity): Post {
        val author = requireNotNull(userDao.getById(model.userId))
        val likedUsers = likeDao.getByPostId(model.postId)
        val comments = commentDao.getByPostId(model.postId)

        return Post(
            id = model.postId,
            userId = model.userId,
            title = model.title,
            text = model.text,
            _date = model.date,
            edited = model.edited,
            author = userEntityMapper.toDomainModel(author),
            likedUsers = userEntityMapper.toDomainModelList(likedUsers),
            comments = commentEntityMapper.toDomainModelList(comments)
        )
    }

    override suspend fun fromDomainModel(domainModel: Post): PostEntity =
        PostEntity(
            postId = domainModel.id,
            userId = domainModel.userId,
            title = domainModel.title,
            text = domainModel.text,
            date = domainModel._date,
            edited = domainModel.edited
        )
}