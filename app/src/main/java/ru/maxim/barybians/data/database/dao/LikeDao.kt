package ru.maxim.barybians.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.data.database.model.LikeEntity.Contract.Columns as LikeColumns
import ru.maxim.barybians.data.database.model.UserEntity.Contract.Columns as UserColumns

@Dao
interface LikeDao {

    @Query(
        """SELECT ${UserEntity.tableName}.* 
            FROM ${UserEntity.tableName} INNER JOIN ${LikeEntity.tableName}
                ON ${UserEntity.tableName}.${UserColumns.userId}=${LikeEntity.tableName}.${LikeColumns.userId}
            WHERE ${LikeColumns.postId}=:postId"""
    )
    fun getByPostId(postId: Int): Flow<List<UserEntity>>

    @Query("SELECT * FROM ${LikeEntity.tableName} WHERE ${LikeColumns.postId}=:postId AND ${LikeColumns.userId}=:userId")
    suspend fun getLike(postId: Int, userId: Int): LikeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(likeEntity: LikeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(likeEntities: List<LikeEntity>)

    @Delete
    suspend fun delete(likeEntity: LikeEntity)

    @Query("DELETE FROM ${LikeEntity.tableName} WHERE ${LikeColumns.postId}=:postId AND ${LikeColumns.userId}=:userId")
    suspend fun delete(postId: Int, userId: Int)

    @Query("DELETE FROM ${LikeEntity.tableName} WHERE ${LikeColumns.postId}=:postId")
    suspend fun deleteByPostId(postId: Int)

    @Query("DELETE FROM ${LikeEntity.tableName}")
    suspend fun clear()
}