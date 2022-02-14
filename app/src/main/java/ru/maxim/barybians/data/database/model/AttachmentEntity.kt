package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.database.model.AttachmentEntity.Contract.tableName

@Entity(
    tableName = tableName,
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity.MessageEntityBody::class,
            parentColumns = [AttachmentEntity.Contract.Columns.messageId],
            childColumns = [MessageEntity.Contract.Columns.messageId],
            onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class AttachmentEntity(

    @ColumnInfo(name = Columns.attachmentId)
    @PrimaryKey(autoGenerate = true)
    val attachmentId: Int,

    @ColumnInfo(name = Columns.messageId)
    val messageId: Int,

    @ColumnInfo(name = Columns.type)
    val type: String,
) {
    companion object Contract {
        const val tableName = "message_attachments"

        object Columns {
            const val attachmentId = "attachment_id"
            const val messageId = "message_id"
            const val type = "type"
        }
    }
}