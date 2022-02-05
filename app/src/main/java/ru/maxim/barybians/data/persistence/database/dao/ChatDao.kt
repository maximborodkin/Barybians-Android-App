package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.persistence.database.model.ChatEntity

@Dao
interface ChatDao {

    @Query("SELECT * FROM ${ChatEntity.tableName}")
    fun getChatsList(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatEntity: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatEntities: List<ChatEntity>)

    @Delete
    fun delete(chatEntity: ChatEntity)
}