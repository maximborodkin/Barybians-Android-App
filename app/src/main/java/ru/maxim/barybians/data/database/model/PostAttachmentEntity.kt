package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.barybians.data.database.model.PostAttachmentEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.PostAttachmentEntity.Contract.tableName

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.postId, Columns.attachmentId],
    foreignKeys = [
        ForeignKey(
            entity = PostEntity.PostEntityBody::class,
            parentColumns = [PostEntity.Contract.Columns.postId],
            childColumns = [Columns.postId],
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
            value = [Columns.postId],
            unique = false
        ),
        Index(
            value = [Columns.attachmentId],
            unique = false
        )
    ]
)
data class PostAttachmentEntity(
    @ColumnInfo(name = Columns.postId)
    val postId: Int,

    @ColumnInfo(name = Columns.attachmentId)
    val attachmentId: Int
) {
    companion object Contract {
        const val tableName = "post_attachments"

        object Columns {
            const val postId = PostEntity.Contract.Columns.postId
            const val attachmentId = AttachmentEntity.Contract.Columns.attachmentId
        }
    }
}