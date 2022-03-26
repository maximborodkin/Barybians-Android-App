package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.PostEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.PostEntity.PostEntityBody

@Dao
abstract class PostDao {

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} ORDER BY ${Columns.date} DESC")
    abstract fun feedPagingSource(): PagingSource<Int, PostEntity>

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId ORDER BY ${Columns.date} DESC")
    abstract fun userPostsPagingSource(userId: Int): PagingSource<Int, PostEntity>

    @Query("SELECT COUNT(*) FROM ${PostEntity.tableName}")
    abstract fun feedPostsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun userPostsCount(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract fun checkPost(postId: Int): Int

    suspend fun save(
        postEntity: PostEntity,
        attachmentDao: AttachmentDao,
        postAttachmentDao: PostAttachmentDao,
        userDao: UserDao,
        commentDao: CommentDao,
        likeDao: LikeDao
    ) {
        userDao.save(postEntity.likes + postEntity.author)
        if (checkPost(postEntity.post.postId) > 0) {
            update(postEntity.post)
        } else {
            insert(postEntity.post)
        }
        commentDao.save(postEntity.comments, userDao)
        postAttachmentDao.save(postEntity.attachments, postEntity.post.postId, attachmentDao)
        likeDao.insert(postEntity.likes.map { like ->
            LikeEntity(postId = postEntity.post.postId, userId = like.userId)
        })
    }

    suspend fun save(
        postEntities: List<PostEntity>,
        attachmentDao: AttachmentDao,
        postAttachmentDao: PostAttachmentDao,
        userDao: UserDao,
        commentDao: CommentDao,
        likeDao: LikeDao
    ) =
        postEntities.forEach { post -> save(post, attachmentDao, postAttachmentDao, userDao, commentDao, likeDao) }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(postEntity: PostEntityBody)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntityBody)

    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun clear()

    @Query("DELETE FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract suspend fun clear(postId: Int)
}