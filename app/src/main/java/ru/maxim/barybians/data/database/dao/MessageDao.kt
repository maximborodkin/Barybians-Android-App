package ru.maxim.barybians.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.MessageEntity
import ru.maxim.barybians.data.database.model.MessageEntity.Contract.Columns

@Dao
interface MessageDao {

    @Transaction
    @Query(
        """SELECT * FROM ${MessageEntity.tableName} WHERE 
        ${Columns.senderId}=:firsUserId AND ${Columns.receiverId}=:secondUserId OR
        ${Columns.senderId}=:secondUserId AND ${Columns.receiverId}=:firsUserId
        ORDER BY ${Columns.time}"""
    )
    fun getChatMessages(firsUserId: Int, secondUserId: Int): Flow<List<MessageEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageEntity: MessageEntity.MessageEntityBody)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageEntities: List<MessageEntity.MessageEntityBody>)

    @Transaction
    @Delete
    suspend fun delete(messageEntity: MessageEntity.MessageEntityBody)
}