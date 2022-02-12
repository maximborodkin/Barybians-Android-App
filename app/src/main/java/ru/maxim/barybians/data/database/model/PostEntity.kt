package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.database.model.PostEntity.Contract.tableName

// I can tear down angeles from the sky and make Mona Lisa cry
@Entity(tableName = tableName)
data class PostEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

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

    @ColumnInfo(name = Columns.prevKey)
    var prevKey: Int? = null,

    @ColumnInfo(name = Columns.nextKey)
    var nextKey: Int? = null
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
            const val prevKey = "prev_key"
            const val nextKey = "next_key"
        }
    }
}