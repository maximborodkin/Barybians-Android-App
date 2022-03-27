package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.barybians.data.database.model.CommentAttachmentEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.CommentAttachmentEntity.Contract.tableName

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.commentId, Columns.attachmentId],
    foreignKeys = [
        ForeignKey(
            entity = CommentEntity.CommentEntityBody::class,
            parentColumns = [CommentEntity.Contract.Columns.commentId],
            childColumns = [Columns.commentId],
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
            value = [Columns.commentId],
            unique = false
        ),
        Index(
            value = [Columns.attachmentId],
            unique = false
        )
    ]
)
data class CommentAttachmentEntity(
    @ColumnInfo(name = Columns.commentId)
    val commentId: Int,

    @ColumnInfo(name = Columns.attachmentId)
    val attachmentId: Int
) {
    companion object Contract {
        const val tableName = "comment_attachments"

        object Columns {
            const val commentId = CommentEntity.Contract.Columns.commentId
            const val attachmentId = AttachmentEntity.Contract.Columns.attachmentId
        }
    }
}