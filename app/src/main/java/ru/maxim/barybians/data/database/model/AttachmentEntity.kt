package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.database.model.AttachmentEntity.Contract.tableName

@Entity(
    tableName = tableName,
    foreignKeys = [

    ],
    indices = [

    ]
)
data class AttachmentEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Columns.attachmentId)
    val attachmentId: Int,

    @ColumnInfo(name = Columns.type)
    val type: String,

    @ColumnInfo(name = Columns.style)
    val style: String?,

    @ColumnInfo(name = Columns.pack)
    val pack: String?,

    @ColumnInfo(name = Columns.sticker)
    val sticker: Int?,

    @ColumnInfo(name = Columns.length)
    val length: Int,

    @ColumnInfo(name = Columns.offset)
    val offset: Int,

    @ColumnInfo(name = Columns.url)
    val url: String?,

    @ColumnInfo(name = Columns.title)
    val title: String?,

    @ColumnInfo(name = Columns.fileSize)
    val fileSize: Long?,

    @ColumnInfo(name = Columns.extension)
    val extension: String?,

    @ColumnInfo(name = Columns.description)
    val description: String?,

    @ColumnInfo(name = Columns.image)
    val image: String?,

    @ColumnInfo(name = Columns.favicon)
    val favicon: String?,
) {
    companion object Contract {
        const val tableName = "attachments"

        object Columns {
            const val attachmentId = "attachment_id"
            const val type = "type"
            const val style = "style"
            const val pack = "pack"
            const val sticker = "sticker"
            const val length = "length"
            const val offset = "offset"
            const val url = "url"
            const val title = "title"
            const val fileSize = "file_size"
            const val extension = "extension"
            const val description = "description"
            const val image = "image"
            const val favicon = "favicon"
        }
    }
}