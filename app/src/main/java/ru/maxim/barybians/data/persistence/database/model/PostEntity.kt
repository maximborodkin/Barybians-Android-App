package ru.maxim.barybians.data.persistence.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.persistence.database.model.PostEntity.Contract.tableName

@Entity(tableName = tableName)
data class PostEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Columns.postId)
    val postId: Int,

    @ColumnInfo(name = Columns.userId)
    val userId: Int,

    @ColumnInfo(name = Columns.title)
    var title: String?,

    @ColumnInfo(name = Columns.text)
    var text: String,

    @ColumnInfo(name = Columns.date)
    var date: Long,

    @ColumnInfo(name = Columns.edited)
    val edited: Int,
) {

    companion object Contract {
        const val tableName = "posts"

        object Columns {
            const val postId = "post_id"
            const val userId = "user_id"
            const val title = "title"
            const val text = "text"
            const val date = "date"
            const val edited = "edited"
        }
    }
}