package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.CommentEntity.Contract.Columns

@Dao
interface CommentDao {

    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId ORDER BY ${Columns.date} DESC")
    fun pagingSource(postId: Int): PagingSource<Int, CommentEntity>

    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    suspend fun getByPostId(postId: Int): List<CommentEntity>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commentEntity: CommentEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commentEntities: List<CommentEntity>)

    @Transaction
    @Delete
    suspend fun delete(commentEntity: CommentEntity)

    @Transaction
    @Query("DELETE FROM ${CommentEntity.tableName}")
    suspend fun delete()

    @Transaction
    @Query("DELETE FROM ${CommentEntity.tableName} WHERE ${Columns.commentId}=:commentId")
    suspend fun delete(commentId: Int)

    @Transaction
    @Query("DELETE FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    suspend fun deleteByPostId(postId: Int)
}