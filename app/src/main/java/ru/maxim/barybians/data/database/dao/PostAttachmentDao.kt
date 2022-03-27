package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.data.database.model.PostAttachmentEntity

@Dao
abstract class PostAttachmentDao {

    private suspend fun save(attachmentEntity: AttachmentEntity, postId: Int, attachmentDao: AttachmentDao) {
        val attachmentId = attachmentDao.save(attachmentEntity)
        insert(PostAttachmentEntity(postId = postId, attachmentId = attachmentId))
    }

    suspend fun save(attachmentEntity: List<AttachmentEntity>, postId: Int, attachmentDao: AttachmentDao) =
        attachmentEntity.forEach { attachment -> save(attachment, postId, attachmentDao) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(attachmentEntity: PostAttachmentEntity)

    @Delete
    abstract suspend fun delete(attachmentEntity: PostAttachmentEntity)
}