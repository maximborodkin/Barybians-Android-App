package ru.maxim.barybians.data.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION

data class CommentEntity(
    @Embedded
    val comment: CommentEntityBody,

    @Relation(
        entity = AttachmentEntity::class,
        parentColumn = CommentAttachmentEntity.Contract.Columns.commentId,
        entityColumn = CommentAttachmentEntity.Contract.Columns.attachmentId,
        associateBy = Junction(CommentAttachmentEntity::class)
    )
    val attachments: List<AttachmentEntity>,

    @Relation(
        entity = UserEntity::class,
        parentColumn = Columns.userId,
        entityColumn = UserEntity.Contract.Columns.userId
    )
    val author: UserEntity
) {
    @Entity(
        tableName = tableName,
        foreignKeys = [
            ForeignKey(
                entity = UserEntity::class,
                parentColumns = [UserEntity.Contract.Columns.userId],
                childColumns = [Columns.userId],
                onDelete = CASCADE, onUpdate = NO_ACTION
            )
        ],
        indices = [
            Index(
                value = [Columns.userId],
                unique = false
            )
        ]
    )
    data class CommentEntityBody(

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
    )

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