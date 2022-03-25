package ru.maxim.barybians.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ru.maxim.barybians.data.database.model.AttachmentEntity

@Dao
interface AttachmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachmentEntity: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachmentEntities: List<AttachmentEntity>)

    @Delete
    suspend fun delete(attachmentEntity: AttachmentEntity)
}