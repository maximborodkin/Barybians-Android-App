package ru.maxim.barybians.data.persistence.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.PostEntity.Contract.Columns
import ru.maxim.barybians.domain.model.Post

@Dao
abstract class PostDao {

    @Query("SELECT * FROM ${PostEntity.tableName}")
    abstract fun getFeed(): List<PostEntity>

    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun getByUserId(userId: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntity)

    @Query("SELECT * FROM ${PostEntity.tableName}")
    abstract fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(postEntities: List<PostEntity>)

    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun clearAll()
}