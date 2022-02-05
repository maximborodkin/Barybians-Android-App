package ru.maxim.barybians.data.persistence.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.maxim.barybians.data.persistence.database.model.LikeEntity.Contract.Columns
import ru.maxim.barybians.data.persistence.database.model.LikeEntity.Contract.tableName

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.postId, Columns.userId]
)
data class LikeEntity(
    @ColumnInfo(name = Columns.postId)
    val postId: Int,

    @ColumnInfo(name = Columns.userId)
    val userId: Int
) {
    companion object Contract {
        const val tableName = "likes"

        object Columns {
            const val postId = "post_id"
            const val userId = "user_id"
        }
    }
}