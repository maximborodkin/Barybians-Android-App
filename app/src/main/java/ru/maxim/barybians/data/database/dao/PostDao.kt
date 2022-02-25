package ru.maxim.barybians.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.CommentEntity
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

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract fun getById(postId: Int): PostEntityBody?

    suspend fun save(postEntity: PostEntity, userDao: UserDao, commentDao: CommentDao, likeDao: LikeDao) {
        userDao.save(postEntity.likes + postEntity.author)
        commentDao.save(postEntity.comments, userDao)
        if (getById(postEntity.post.postId) != null) {
            update(postEntity.post)
        } else {
            insert(postEntity.post)
        }
        likeDao.insert(postEntity.likes.map { like ->
            LikeEntity(postId = postEntity.post.postId, userId = like.userId)
        })
    }

    suspend fun savePosts(postEntities: List<PostEntity>, userDao: UserDao, commentDao: CommentDao, likeDao: LikeDao) =
        postEntities.forEach { post -> save(post, userDao, commentDao, likeDao) }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(postEntity: PostEntityBody)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntityBody)

    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun delete()

    @Query("DELETE FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract suspend fun delete(postId: Int)
}