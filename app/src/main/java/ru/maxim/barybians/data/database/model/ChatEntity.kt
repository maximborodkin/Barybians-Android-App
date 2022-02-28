package ru.maxim.barybians.data.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.tableName
import ru.maxim.barybians.data.database.model.MessageEntity.MessageEntityBody

@Entity(
    tableName = tableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserEntity.Contract.Columns.userId],
            childColumns = [Columns.secondUserId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = MessageEntityBody::class,
            parentColumns = [MessageEntity.Contract.Columns.messageId],
            childColumns = [Columns.lastMessageId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        )
    ],
    indices = [
        Index(
            value = [Columns.secondUserId],
            unique = false
        ),
        Index(
            value = [Columns.lastMessageId],
            unique = false
        )
    ]
)
data class ChatEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Columns.secondUserId)
    val secondUser: Int,

    @ColumnInfo(name = Columns.lastMessageId)
    val lastMessage: Int,

    @ColumnInfo(name = Columns.unreadCount)
    val unreadCount: Int
) {

    companion object Contract {
        const val tableName = "chats"

        object Columns {
            const val secondUserId = "second_user_id"
            const val lastMessageId = "last_message_id"
            const val unreadCount = "unread_count"
        }
    }
}