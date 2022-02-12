package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.PostEntity.Contract.Columns

@Dao
interface PostDao {

    @Query("SELECT * FROM ${PostEntity.tableName}")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    fun getByUserId(userId: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntity: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntities: List<PostEntity>)

    @Query("DELETE FROM ${PostEntity.tableName}")
    suspend fun delete()

    @Query("DELETE FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    suspend fun delete(postId: Int)
}