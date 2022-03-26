package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.data.database.model.PostAttachmentEntity

@Dao
abstract class PostAttachmentDao {

    private suspend fun save(postAttachment: AttachmentEntity, postId: Int, attachmentDao: AttachmentDao) {
        val attachmentId = attachmentDao.save(postAttachment)
        insert(PostAttachmentEntity(postId = postId, attachmentId = attachmentId))
    }

    suspend fun save(postAttachments: List<AttachmentEntity>, postId: Int, attachmentDao: AttachmentDao) =
        postAttachments.forEach { attachment -> save(attachment, postId, attachmentDao) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(attachmentEntity: PostAttachmentEntity)

    @Delete
    abstract suspend fun delete(attachmentEntity: PostAttachmentEntity)
}