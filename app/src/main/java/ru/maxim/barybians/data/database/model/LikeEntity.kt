package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import ru.maxim.barybians.data.database.model.LikeEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.LikeEntity.Contract.tableName

@Entity(
    tableName = tableName,
    primaryKeys = [Columns.postId, Columns.userId],
    foreignKeys = [
        ForeignKey(
            entity = PostEntity.PostEntityBody::class,
            parentColumns = [PostEntity.Contract.Columns.postId],
            childColumns = [LikeEntity.Contract.Columns.postId],
            onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserEntity.Contract.Columns.userId],
            childColumns = [LikeEntity.Contract.Columns.userId],
            onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.NO_ACTION
        ),
    ]
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
            const val postId = PostEntity.Contract.Columns.postId
            const val userId = UserEntity.Contract.Columns.userId
        }
    }
}