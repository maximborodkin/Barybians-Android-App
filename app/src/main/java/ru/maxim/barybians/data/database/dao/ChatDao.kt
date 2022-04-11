package ru.maxim.barybians.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.ChatEntity
import ru.maxim.barybians.data.database.model.ChatEntity.ChatEntityBody
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.Columns as ChatColumns

@Dao
abstract class ChatDao {

    @Transaction
    @Query("SELECT * FROM ${ChatEntity.tableName}")
    abstract fun getChatsList(): Flow<List<ChatEntity>>

    @Transaction
    @Query("SELECT * FROM ${ChatEntity.tableName} WHERE ${ChatColumns.secondUserId}=:userId")
    abstract fun getByInterlocutorId(userId: Int): ChatEntity?

    @Delete
    abstract suspend fun delete(chatEntity: ChatEntityBody)

    suspend fun save(
        chatEntity: ChatEntity,
        messageDao: MessageDao,
        userDao: UserDao,
        attachmentDao: AttachmentDao,
        messageAttachmentDao: MessageAttachmentDao
    ) {
        userDao.save(chatEntity.secondUser)
        messageDao.save(chatEntity.lastMessage, attachmentDao, messageAttachmentDao)
        if (getByInterlocutorId(chatEntity.secondUser.userId) != null) {
            update(chatEntity.chat)
        } else {
            insert(chatEntity.chat)
        }
    }

    suspend fun save(
        chatEntities: List<ChatEntity>,
        messageDao: MessageDao,
        userDao: UserDao,
        attachmentDao: AttachmentDao,
        messageAttachmentDao: MessageAttachmentDao
    ) =
        chatEntities.forEach { chat -> save(chat, messageDao, userDao, attachmentDao, messageAttachmentDao) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(chatEntity: ChatEntityBody)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(chatEntity: ChatEntityBody)

    @Query("DELETE FROM ${ChatEntity.tableName}")
    abstract suspend fun clear()
}