package ru.maxim.barybians.data.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION

data class MessageEntity(
    @Embedded val message: MessageEntityBody,

    @Relation(
        entity = AttachmentEntity::class,
        parentColumn = MessageAttachmentEntity.Contract.Columns.messageId,
        entityColumn = MessageAttachmentEntity.Contract.Columns.attachmentId,
        associateBy = Junction(MessageAttachmentEntity::class)
    )
    val attachments: List<AttachmentEntity>
) {
    @Entity(
        tableName = tableName,
        foreignKeys = [
            ForeignKey(
                entity = UserEntity::class,
                parentColumns = [UserEntity.Contract.Columns.userId],
                childColumns = [Columns.senderId],
                onDelete = CASCADE, onUpdate = NO_ACTION
            ),
            ForeignKey(
                entity = UserEntity::class,
                parentColumns = [UserEntity.Contract.Columns.userId],
                childColumns = [Columns.receiverId],
                onDelete = CASCADE, onUpdate = NO_ACTION
            ),
        ],
        indices = [
            Index(
                value = [Columns.senderId],
                unique = false
            ),
            Index(
                value = [Columns.receiverId],
                unique = false
            )
        ]
    )
    data class MessageEntityBody(

        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = Columns.messageId)
        val messageId: Int,

        @ColumnInfo(name = Columns.senderId)
        val senderId: Int,

        @ColumnInfo(name = Columns.receiverId)
        val receiverId: Int,

        @ColumnInfo(name = Columns.text)
        val text: String,

        @ColumnInfo(name = Columns.time)
        val date: Long,

        @ColumnInfo(name = Columns.status)
        val status: Int,

        @ColumnInfo(name = Columns.prevPage)
        var prevPage: Int? = null,

        @ColumnInfo(name = Columns.nextPage)
        var nextPage: Int? = null,
    )

    companion object Contract {
        const val tableName = "messages"

        object Columns {
            const val messageId = "message_id"
            const val senderId = "sender_id"
            const val receiverId = "receiver_id"
            const val text = "text"
            const val time = "date"
            const val status = "status"
            const val prevPage = "prev_page"
            const val nextPage = "next_page"
        }
    }
}