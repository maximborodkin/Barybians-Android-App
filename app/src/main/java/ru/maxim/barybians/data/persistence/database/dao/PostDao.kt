package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.*
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.PostEntity.Contract.Columns

@Dao
interface PostDao {

    @Query("SELECT * FROM ${PostEntity.tableName} ORDER BY ${Columns.date}")
    suspend fun getFeed(): List<PostEntity>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    suspend fun getByUserId(userId: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntity: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntities: List<PostEntity>)

    @Delete
    suspend fun delete(postEntity: PostEntity)
}