package ru.maxim.barybians.data.persistence.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.persistence.database.model.MessageEntity.Contract.tableName

@Entity(tableName = tableName)
data class MessageEntity(

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
    val time: Long,

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
            const val time = "time"
            const val unread = "unread"
        }
    }
}