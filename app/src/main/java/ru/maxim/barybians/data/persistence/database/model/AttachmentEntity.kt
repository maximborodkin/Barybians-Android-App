package ru.maxim.barybians.data.persistence.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.persistence.database.model.AttachmentEntity.Contract.tableName

@Entity(tableName = tableName)
data class AttachmentEntity (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.attachmentId)
    val attachmentId: Int,

    @ColumnInfo(name = Columns.messageId)
    val messageId: Int,

    @ColumnInfo(name = Columns.type)
    val type: String,

//    val offset: Int?,
//    val length: Int?,
//    val style: String?,
//
//    val url: String?,
//    val title: String?,
//    val description: String?,
//    val image: String?,
//    val favicon: String?,
//    val timestamp: Long?
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