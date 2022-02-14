package ru.maxim.barybians.data.database.model

import androidx.room.*
import ru.maxim.barybians.data.database.model.MessageEntity.MessageEntityBody.Contract.tableName

data class MessageEntity(
    @Embedded val message: MessageEntityBody,

    @Relation(
        entity = AttachmentEntity::class,
        entityColumn = AttachmentEntity.Contract.Columns.messageId,
        parentColumn = MessageEntityBody.Contract.Columns.messageId
    )
    val attachments: List<AttachmentEntity>
) {
    @Entity(tableName = tableName)
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

        @ColumnInfo(name = Columns.unread)
        val unread: Int,
    ) {
        companion object Contract {
            const val tableName = "messages"

            object Columns {
                const val messageId = "message_id"
                const val senderId = "sender_id"
                const val receiverId = "receiver_id"
                const val text = "text"
                const val time = "date"
                const val unread = "unread"
            }
        }
    }
}