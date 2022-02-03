package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.UserEntity
import ru.maxim.barybians.data.persistence.database.model.UserEntity.Contract.Columns

@Dao
interface UserDao {

    @Query("SELECT * FROM ${UserEntity.tableName}")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM ${UserEntity.tableName} WHERE ${Columns.userId}=:userId")
    suspend fun getById(userId: Int): UserEntity?

}