package ru.maxim.barybians.data.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import ru.maxim.barybians.data.persistence.database.model.AttachmentEntity
import ru.maxim.barybians.data.persistence.database.model.AttachmentEntity.Contract.Columns

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM ${AttachmentEntity.tableName} WHERE ${Columns.messageId}=:messageId")
    suspend fun getByMessageId(messageId: Int): List<AttachmentEntity>
}