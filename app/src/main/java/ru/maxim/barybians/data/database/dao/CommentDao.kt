package ru.maxim.barybians.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.CommentEntity.CommentEntityBody
import ru.maxim.barybians.data.database.model.CommentEntity.Contract.Columns

@Dao
abstract class CommentDao {

    @Transaction
    @Query(
        """
        SELECT * FROM ${CommentEntity.tableName}
        WHERE ${Columns.postId}=:postId 
        ORDER BY
            CASE WHEN :sortingDirection = 1 THEN ${Columns.date} END ASC, 
            CASE WHEN :sortingDirection = 0 THEN ${Columns.date} END DESC
    """
    )
    abstract fun getByPostId(postId: Int, sortingDirection: Boolean): LiveData<List<CommentEntity>>

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

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(commentEntity: CommentEntityBody)

    @Delete
    abstract suspend fun delete(commentEntity: CommentEntityBody)

    @Query("DELETE FROM ${CommentEntity.tableName} WHERE ${Columns.commentId}=:commentId")
    abstract suspend fun delete(commentId: Int)

    @Query("DELETE FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract suspend fun deleteByPostId(postId: Int)

    @Query("DELETE FROM ${CommentEntity.tableName}")
    abstract suspend fun clear()
}