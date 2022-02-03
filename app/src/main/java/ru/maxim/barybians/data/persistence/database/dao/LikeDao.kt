package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.LikeEntity
import ru.maxim.barybians.data.persistence.database.model.LikeEntity.Contract.Columns as Like
import ru.maxim.barybians.data.persistence.database.model.UserEntity.Contract.Columns as User
import ru.maxim.barybians.data.persistence.database.model.UserEntity

@Dao
interface LikeDao {

    @Query(
        """SELECT * FROM 
            ${UserEntity.tableName} INNER JOIN ${LikeEntity.tableName}
                ON ${UserEntity.tableName}.${User.userId}=${LikeEntity.tableName}.${Like.userId}
            WHERE ${Like.postId}=:postId"""
    )
    suspend fun getByPostId(postId: Int): List<UserEntity>
}