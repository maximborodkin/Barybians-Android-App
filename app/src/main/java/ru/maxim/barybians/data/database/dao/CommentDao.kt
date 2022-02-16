package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.CommentEntity.CommentEntityBody
import ru.maxim.barybians.data.database.model.CommentEntity.Contract.Columns

@Dao
abstract class CommentDao {

    @Transaction
    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId ORDER BY ${Columns.date} DESC")
    abstract fun pagingSource(postId: Int): PagingSource<Int, CommentEntity>

    @Transaction
    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract fun getByPostId(postId: Int): PagingSource<Int, CommentEntity>

    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.commentId}=:commentId")
    abstract fun getById(commentId: Int): CommentEntityBody?

    suspend fun save(commentEntity: CommentEntity, userDao: UserDao) {
        userDao.save(commentEntity.author)
        if (getById(commentEntity.comment.commentId) != null) {
            update(commentEntity.comment)
        } else {
            insert(commentEntity.comment)
        }
    }

    suspend fun save(commentEntities: List<CommentEntity>, userDao: UserDao) {
        commentEntities.forEach { comment ->
            userDao.save(comment.author)
            save(comment, userDao)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(commentEntity: CommentEntityBody)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(commentEntities: List<CommentEntityBody>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(commentEntity: CommentEntityBody)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(commentEntity: List<CommentEntityBody>)

    @Delete
    abstract suspend fun delete(commentEntity: CommentEntityBody)

    @Query("DELETE FROM ${CommentEntity.tableName} WHERE ${Columns.commentId}=:commentId")
    abstract suspend fun delete(commentId: Int)

    @Query("DELETE FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract suspend fun deleteByPostId(postId: Int)

    @Query("DELETE FROM ${CommentEntity.tableName}")
    abstract suspend fun clear()
}