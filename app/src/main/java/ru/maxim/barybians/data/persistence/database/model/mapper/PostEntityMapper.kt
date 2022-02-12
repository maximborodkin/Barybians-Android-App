package ru.maxim.barybians.data.persistence.database.model.mapper

import androidx.room.withTransaction
import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.persistence.database.BarybiansDatabase
import ru.maxim.barybians.data.persistence.database.dao.CommentDao
import ru.maxim.barybians.data.persistence.database.dao.LikeDao
import ru.maxim.barybians.data.persistence.database.dao.UserDao
import ru.maxim.barybians.data.persistence.database.model.LikeEntity
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.domain.model.Post
import timber.log.Timber
import java.lang.StringBuilder
import javax.inject.Inject

class PostEntityMapper @Inject constructor(
    private val database: BarybiansDatabase,
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

    override suspend fun fromDomainModel(domainModel: Post): PostEntity {
        /*
        * The game is over
        * No more rounds to play, it's time to pay
        * Who's got the Joker?
        * To kill the lies and make your hurt recall
        * There is no other sky to fall
        * */
        database.withTransaction {
            userDao.insert(userEntityMapper.fromDomainModel(domainModel.author))
            userDao.insert(userEntityMapper.fromDomainModelList(domainModel.likedUsers))
            likeDao.insert(domainModel.likedUsers.map { user -> LikeEntity(postId = domainModel.id, userId = user.id) })
            commentDao.insert(commentEntityMapper.fromDomainModelList(domainModel.comments))
        }

        return PostEntity(
            id = 0,
            postId = domainModel.id,
            userId = domainModel.userId,
            title = domainModel.title,
            text = domainModel.text,
            date = domainModel._date,
            edited = domainModel.edited
        )
    }

    override suspend fun toDomainModelList(model: List<PostEntity>): List<Post> {
        val str = StringBuilder()
        model.forEach { str.append(it.postId) }
        Timber.d("XXX toDomainModelList ${model.size} $str")
        return super.toDomainModelList(model)
    }
}