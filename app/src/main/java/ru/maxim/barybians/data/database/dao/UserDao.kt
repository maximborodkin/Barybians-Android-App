package ru.maxim.barybians.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.data.database.model.UserEntity.Contract.Columns

@Dao
abstract class UserDao {

    @Query("SELECT * FROM ${UserEntity.tableName}")
    abstract fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM ${UserEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun getById(userId: Int): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM ${UserEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun checkUser(userId: Int): Int

    suspend fun save(userEntity: UserEntity) {
        if (checkUser(userEntity.userId) > 0) {
            update(userEntity)
        } else {
            insert(userEntity)
        }
    }

    suspend fun save(userEntity: List<UserEntity>) = userEntity.forEach { user -> save(user) }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(userEntity: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(userEntity: UserEntity)
}