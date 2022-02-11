package ru.maxim.barybians.data.persistence.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.PostEntity.Contract.Columns

@Dao
abstract class PostDao {

    @Query("SELECT * FROM ${PostEntity.tableName}")
    abstract fun getFeed(): List<PostEntity>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun getByUserId(userId: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntity)

    @Query("SELECT * FROM ${PostEntity.tableName} ORDER BY ${Columns.date} DESC")
    abstract fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(postEntities: List<PostEntity>)

    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun clearAll()
}