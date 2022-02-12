package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.data.database.model.LikeEntity.Contract as Like
import ru.maxim.barybians.data.database.model.UserEntity.Contract as User

@Dao
interface LikeDao {

    @Query(
        """SELECT ${User.tableName}.* FROM 
            ${User.tableName} INNER JOIN ${Like.tableName}
                ON ${User.tableName}.${User.Columns.userId}=${Like.tableName}.${Like.Columns.userId}
            WHERE ${Like.Columns.postId}=:postId"""
    )
    suspend fun getByPostId(postId: Int): List<UserEntity>

    @Query("SELECT * FROM ${Like.tableName} WHERE ${Like.Columns.postId}=:postId AND ${Like.Columns.userId}=:userId")
    suspend fun getLike(postId: Int, userId: Int): LikeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(likeEntity: LikeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(likeEntities: List<LikeEntity>)

    @Delete
    suspend fun delete(likeEntity: LikeEntity)

    @Query("DELETE FROM ${LikeEntity.tableName}")
    suspend fun delete()

    @Query("DELETE FROM ${Like.tableName} WHERE ${Like.Columns.postId}=:postId AND ${Like.Columns.userId}=:userId")
    suspend fun removeLike(postId: Int, userId: Int)
}