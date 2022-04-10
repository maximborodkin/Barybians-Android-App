package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.PostEntity.Contract.Columns as PostColumns
import ru.maxim.barybians.data.database.model.PostEntity.PostEntityBody

@Dao
abstract class PostDao {

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} ORDER BY ${PostColumns.date} DESC")
    abstract fun feedPagingSource(): PagingSource<Int, PostEntity>

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${PostColumns.userId}=:userId ORDER BY ${PostColumns.date} DESC")
    abstract fun userPostsPagingSource(userId: Int): PagingSource<Int, PostEntity>

    @Query("SELECT COUNT(*) FROM ${PostEntity.tableName}")
    abstract fun feedPostsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ${PostEntity.tableName} WHERE ${PostColumns.userId}=:userId")
    abstract fun userPostsCount(userId: Int): Flow<Int>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${PostColumns.postId}=:postId")
    abstract fun getById(postId: Int): PostEntity?

    suspend fun save(
        postEntity: PostEntity,
        attachmentDao: AttachmentDao,
        postAttachmentDao: PostAttachmentDao,
        commentAttachmentDao: CommentAttachmentDao,
        userDao: UserDao,
        commentDao: CommentDao,
        likeDao: LikeDao
    ) {
        userDao.save(postEntity.likes + postEntity.author)
        if (getById(postEntity.post.postId) != null) {
            update(postEntity.post)
        } else {
            insert(postEntity.post)
        }
        commentDao.save(postEntity.comments, attachmentDao, commentAttachmentDao, userDao)
        postAttachmentDao.save(postEntity.attachments, postEntity.post.postId, attachmentDao)
        likeDao.insert(postEntity.likes.map { like ->
            LikeEntity(postId = postEntity.post.postId, userId = like.userId)
        })
    }

    suspend fun save(
        postEntities: List<PostEntity>,
        attachmentDao: AttachmentDao,
        postAttachmentDao: PostAttachmentDao,
        commentAttachmentDao: CommentAttachmentDao,
        userDao: UserDao,
        commentDao: CommentDao,
        likeDao: LikeDao
    ) =
        postEntities.forEach { post ->
            save(
                postEntity = post,
                attachmentDao = attachmentDao,
                postAttachmentDao = postAttachmentDao,
                commentAttachmentDao = commentAttachmentDao,
                userDao = userDao,
                commentDao = commentDao,
                likeDao = likeDao
            )
        }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(postEntity: PostEntityBody)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntityBody)

    @Query("DELETE FROM ${PostEntity.tableName} WHERE ${PostColumns.postId}=:postId")
    abstract suspend fun clear(postId: Int)

    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun clear()
}