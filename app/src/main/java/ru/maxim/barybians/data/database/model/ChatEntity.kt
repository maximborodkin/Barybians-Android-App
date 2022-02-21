package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.tableName
import ru.maxim.barybians.data.database.model.MessageEntity.MessageEntityBody

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.firstUserId, Columns.secondUserId],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserEntity.Contract.Columns.userId],
            childColumns = [Columns.firstUserId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
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
            value = [Columns.firstUserId],
            unique = false
        ),
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

    @ColumnInfo(name = Columns.firstUserId)
    val firstUserId: Int,

    @ColumnInfo(name = Columns.secondUserId)
    val secondUserId: Int,

    @ColumnInfo(name = Columns.lastMessageId)
    val lastMessageId: Int
) {
    companion object Contract {
        const val tableName = "chats"

        object Columns {
            const val firstUserId = "first_user_id"
            const val secondUserId = "second_user_id"
            const val lastMessageId = "last_message_id"
        }
    }
}