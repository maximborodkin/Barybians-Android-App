package ru.maxim.barybians.data.database.model

import androidx.room.*
import ru.maxim.barybians.data.database.model.PostEntityBody.Contract.tableName

data class PostEntity(
    @Embedded val post: PostEntityBody,

    @Relation(
        entity = UserEntity::class,
        entityColumn = UserEntity.Contract.Columns.userId,
        parentColumn = PostEntityBody.Contract.Columns.userId
    )
    @Embedded val author: UserEntity,

    @Relation(
        entity = UserEntity::class,
        entityColumn = LikeEntity.Contract.Columns.userId,
        parentColumn = LikeEntity.Contract.Columns.postId,
        associateBy = Junction(LikeEntity::class)
    )
    val likes: List<UserEntity>,

    @Relation(
        entity = CommentEntity::class,
        entityColumn = CommentEntity.Contract.Columns.postId,
        parentColumn = PostEntityBody.Contract.Columns.postId
    )
    val comments: List<CommentEntity>
)

// I can tear down angeles from the sky and make Mona Lisa cry
@Entity(tableName = tableName)
data class PostEntityBody(

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
            const val edited = "isEdited"
            const val prevKey = "prev_key"
            const val nextKey = "next_key"
        }
    }
}