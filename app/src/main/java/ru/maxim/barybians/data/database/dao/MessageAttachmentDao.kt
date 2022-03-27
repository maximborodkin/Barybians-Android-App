package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.data.database.model.MessageAttachmentEntity

@Dao
abstract class MessageAttachmentDao {

    private suspend fun save(attachmentEntity: AttachmentEntity, messageId: Int, attachmentDao: AttachmentDao) {
        val attachmentId = attachmentDao.save(attachmentEntity)
        insert(MessageAttachmentEntity(messageId = messageId, attachmentId = attachmentId))
    }

    suspend fun save(messageAttachmentEntities: List<AttachmentEntity>, messageId: Int, attachmentDao: AttachmentDao) =
        messageAttachmentEntities.forEach { attachment -> save(attachment, messageId, attachmentDao) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(attachmentEntity: MessageAttachmentEntity)

    @Delete
    abstract suspend fun delete(attachmentEntity: MessageAttachmentEntity)
}