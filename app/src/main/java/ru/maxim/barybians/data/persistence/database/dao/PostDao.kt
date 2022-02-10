package ru.maxim.barybians.data.persistence.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.PostEntity
import ru.maxim.barybians.data.persistence.database.model.PostEntity.Contract.Columns
import ru.maxim.barybians.data.persistence.database.model.PostPagingKey

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

    @Query("SELECT * FROM post_paging_keys WHERE postId=:postId")
    abstract fun keyByPostId(postId: Int): PostPagingKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertKeys(keys: List<PostPagingKey>)

    @Query("DELETE FROM post_paging_keys")
    abstract fun clearKeys()
}