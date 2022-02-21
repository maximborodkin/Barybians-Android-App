package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.data.database.model.UserEntity.Contract.Columns

@Dao
abstract class UserDao {

    @Query("SELECT * FROM ${UserEntity.tableName}")
    abstract fun pagingSource(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM ${UserEntity.tableName} WHERE ${Columns.userId}=:userId")
    abstract fun getById(userId: Int): UserEntity?

    suspend fun save(userEntity: UserEntity) {
        if (getById(userEntity.userId) != null) {
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

    @Delete
    abstract suspend fun delete(userEntity: UserEntity)
}