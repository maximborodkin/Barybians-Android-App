package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.PostEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.UserEntity

@Dao
abstract class PostDao {

    @Query("SELECT COUNT(*) FROM ${PostEntity.tableName}")
    abstract fun getPostsCount(): Int

    @Query("SELECT COUNT(*) FROM ${UserEntity.tableName}")
    abstract fun getUsersCount(): Int

    @Query("SELECT COUNT(*) FROM ${CommentEntity.tableName}")
    abstract fun getCommentsCount(): Int

    @Query("SELECT COUNT(*) FROM ${LikeEntity.tableName}")
    abstract fun getLikesCount(): Int


    @Query("SELECT * FROM ${PostEntity.tableName} ORDER BY ${Columns.date} DESC")
    abstract fun pagingSource(): PagingSource<Int, PostEntity>

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun getByUserId(userId: Int): List<PostEntity>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntity.PostEntityBody)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntities: List<PostEntity.PostEntityBody>)

    @Transaction
    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun delete()

    @Transaction
    @Query("DELETE FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract suspend fun delete(postId: Int)

    suspend fun savePosts(userDao: UserDao, commentDao: CommentDao, postEntities: List<PostEntity>) {
        insert(postEntities.map { it.post })
        userDao.insert(postEntities.flatMap { it.likes })
        commentDao.insert(postEntities.flatMap { it.comments })
    }
}