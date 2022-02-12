package ru.maxim.barybians.data.database.dao

import androidx.room.*
import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.data.database.model.AttachmentEntity.Contract.Columns

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM ${AttachmentEntity.tableName} WHERE ${Columns.messageId}=:messageId")
    suspend fun getByMessageId(messageId: Int): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachmentEntity: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachmentEntities: List<AttachmentEntity>)

    @Delete
    suspend fun delete(attachmentEntity: AttachmentEntity)
}