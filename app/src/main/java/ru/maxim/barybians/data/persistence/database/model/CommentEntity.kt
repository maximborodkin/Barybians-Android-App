package ru.maxim.barybians.data.persistence.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.persistence.database.model.CommentEntity.Contract.tableName

@Entity(tableName = tableName)
data class CommentEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Columns.commentId)
    val commentId: Int,

    @ColumnInfo(name = Columns.postId)
    val postId: Int,

    @ColumnInfo(name = Columns.userId)
    val userId: Int,

    @ColumnInfo(name = Columns.text)
    val text: String,

    @ColumnInfo(name = Columns.date)
    val date: Long
) {
    companion object Contract {
        const val tableName = "comments"

        object Columns {
            const val commentId = "comment_id"
            const val postId = "post_id"
            const val userId = "user_id"
            const val text = "text"
            const val date = "date"
        }
    }
}