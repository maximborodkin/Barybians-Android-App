package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.*
import ru.maxim.barybians.data.persistence.database.model.CommentEntity
import ru.maxim.barybians.data.persistence.database.model.CommentEntity.Contract.Columns

@Dao
interface CommentDao {

    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    suspend fun getByPostId(postId: Int): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commentEntity: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commentEntities: List<CommentEntity>)

    @Delete
    suspend fun delete(commentEntity: CommentEntity)
}