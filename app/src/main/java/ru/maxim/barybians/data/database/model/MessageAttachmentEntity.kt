package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.barybians.data.database.model.MessageAttachmentEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.MessageAttachmentEntity.Contract.tableName

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.messageId, Columns.attachmentId],
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity.MessageEntityBody::class,
            parentColumns = [MessageEntity.Contract.Columns.messageId],
            childColumns = [Columns.messageId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = AttachmentEntity::class,
            parentColumns = [AttachmentEntity.Contract.Columns.attachmentId],
            childColumns = [Columns.attachmentId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
    ],
    indices = [
        Index(
            value = [Columns.messageId],
            unique = false
        ),
        Index(
            value = [Columns.attachmentId],
            unique = false
        )
    ]
)
data class MessageAttachmentEntity(
    @ColumnInfo(name = Columns.messageId)
    val messageId: Int,

    @ColumnInfo(name = Columns.attachmentId)
    val attachmentId: Int
) {
    companion object Contract {
        const val tableName = "message_attachments"

        object Columns {
            const val messageId = MessageEntity.Contract.Columns.messageId
            const val attachmentId = AttachmentEntity.Contract.Columns.attachmentId
        }
    }
}