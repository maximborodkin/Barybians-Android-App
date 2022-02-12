package ru.maxim.barybians.data.persistence.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.PostEntity.Contract.Columns

@Dao
interface PostDao {

    @Query("SELECT * FROM ${PostEntity.tableName}")
    fun getFeed(): List<PostEntity>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.page}=:page")
    fun getFeedPage(page: Int): List<PostEntity>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    fun getByUserId(userId: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntity: PostEntity)

    @Query("SELECT * FROM ${PostEntity.tableName}")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(postEntities: List<PostEntity>)

    @Query("DELETE FROM ${PostEntity.tableName}")
    suspend fun clearAll()
}