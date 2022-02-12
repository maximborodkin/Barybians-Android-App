package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.data.database.model.UserEntity.Contract.Columns

@Dao
interface UserDao {

    @Query("SELECT * FROM ${UserEntity.tableName}")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM ${UserEntity.tableName} WHERE ${Columns.userId}=:userId")
    suspend fun getById(userId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntity: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntities: List<UserEntity>)

    @Delete
    suspend fun delete(userEntity: UserEntity)
}