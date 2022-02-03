package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.CommentEntity
import ru.maxim.barybians.data.persistence.database.model.CommentEntity.Contract.Columns

@Dao
interface CommentDao {

    @Query("SELECT * FROM ${CommentEntity.tableName} WHERE ${Columns.postId}=:postId")
    fun getByPostId(postId: Int): List<CommentEntity>
}