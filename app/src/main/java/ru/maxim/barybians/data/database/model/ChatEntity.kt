package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.ChatEntity.Contract.tableName

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.firstUserId, Columns.secondUserId]
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