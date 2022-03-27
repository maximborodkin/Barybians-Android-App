package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.data.database.model.CommentAttachmentEntity

@Dao
abstract class CommentAttachmentDao {

    private suspend fun save(attachmentEntity: AttachmentEntity, commentId: Int, attachmentDao: AttachmentDao) {
        val attachmentId = attachmentDao.save(attachmentEntity)
        insert(CommentAttachmentEntity(commentId = commentId, attachmentId = attachmentId))
    }

    suspend fun save(attachmentEntity: List<AttachmentEntity>, commentId: Int, attachmentDao: AttachmentDao) =
        attachmentEntity.forEach { attachment -> save(attachment, commentId, attachmentDao) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(attachmentEntity: CommentAttachmentEntity)

    @Delete
    abstract suspend fun delete(attachmentEntity: CommentAttachmentEntity)
}