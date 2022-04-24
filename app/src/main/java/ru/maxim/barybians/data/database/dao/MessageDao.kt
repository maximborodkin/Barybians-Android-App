package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.MessageEntity
import ru.maxim.barybians.data.database.model.MessageEntity.Contract.Columns as MessageColumns
import ru.maxim.barybians.data.database.model.MessageEntity.MessageEntityBody

@Dao
abstract class MessageDao {

    @Transaction
    @Query(
        """SELECT * FROM ${MessageEntity.tableName} WHERE 
        ${MessageColumns.senderId}=:firsUserId AND ${MessageColumns.receiverId}=:secondUserId OR
        ${MessageColumns.senderId}=:secondUserId AND ${MessageColumns.receiverId}=:firsUserId
        ORDER BY ${MessageColumns.time}"""
    )
    abstract fun messagesPagingSource(firsUserId: Int, secondUserId: Int): PagingSource<Int, MessageEntity>

    @Query(
        """SELECT COUNT(*) FROM ${MessageEntity.tableName} WHERE 
        ${MessageColumns.senderId}=:firsUserId AND ${MessageColumns.receiverId}=:secondUserId OR
        ${MessageColumns.senderId}=:secondUserId AND ${MessageColumns.receiverId}=:firsUserId"""
    )
    abstract fun messagesCount(firsUserId: Int, secondUserId: Int): Flow<Int>

    @Query("SELECT * FROM ${MessageEntity.tableName} WHERE ${MessageColumns.messageId}=:messageId")
    abstract fun getById(messageId: Int): MessageEntityBody?

    suspend fun save(
        messageEntity: MessageEntity,
        attachmentDao: AttachmentDao,
        messageAttachmentDao: MessageAttachmentDao
    ) {
        if (getById(messageEntity.message.messageId) != null) {
            update(messageEntity.message)
        } else {
            insert(messageEntity.message)
        }
        messageAttachmentDao.save(messageEntity.attachments, messageEntity.message.messageId, attachmentDao)
    }

    suspend fun save(
        messageEntities: List<MessageEntity>,
        attachmentDao: AttachmentDao,
        messageAttachmentDao: MessageAttachmentDao
    ) =
        messageEntities.forEach { message -> save(message, attachmentDao, messageAttachmentDao) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(messageEntity: MessageEntityBody)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(messageEntity: MessageEntityBody)

    @Delete
    abstract suspend fun delete(messageEntity: MessageEntityBody)

    @Query(
        """DELETE FROM ${MessageEntity.tableName} WHERE 
        ${MessageColumns.senderId}=:firsUserId AND ${MessageColumns.receiverId}=:secondUserId OR
        ${MessageColumns.senderId}=:secondUserId AND ${MessageColumns.receiverId}=:firsUserId"""
    )
    abstract fun clear(firsUserId: Int, secondUserId: Int)
}