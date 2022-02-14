package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.delay
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.PostEntity.Contract.Columns
import javax.inject.Inject

@Dao
abstract class PostDao {

    @Query("SELECT * FROM ${PostEntity.tableName}")
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

    suspend fun savePost(userDao: UserDao, commentDao: CommentDao, postEntity: PostEntity) {
        insert(postEntity.post)
        userDao.insert(postEntity.likes)
        commentDao.insert(postEntity.comments)
    }

    suspend fun savePosts(userDao: UserDao, commentDao: CommentDao, postEntities: List<PostEntity>) {
        insert(postEntities.map { it.post })
//        userDao.insert(postEntities.flatMap { it.likes })
//        commentDao.insert(postEntities.flatMap { it.comments })
    }
}