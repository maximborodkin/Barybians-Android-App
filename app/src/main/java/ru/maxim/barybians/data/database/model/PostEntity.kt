package ru.maxim.barybians.data.database.model

import androidx.room.*

data class PostEntity(
    @Embedded val post: PostEntityBody,

    @Relation(
        entity = UserEntity::class,
        parentColumn = Columns.userId,
        entityColumn = UserEntity.Contract.Columns.userId
    )
    val author: UserEntity,

    @Relation(
        entity = UserEntity::class,
        parentColumn = LikeEntity.Contract.Columns.postId,
        entityColumn = LikeEntity.Contract.Columns.userId,
        associateBy = Junction(LikeEntity::class)
    )
    val likes: List<UserEntity>,

    @Relation(
        entity = CommentEntity::class,
        parentColumn = Columns.postId,
        entityColumn = CommentEntity.Contract.Columns.postId
    )
    val comments: List<CommentEntity>
) {

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

        @ColumnInfo(name = Columns.prevPage)
        var prevPage: Int? = null,

        @ColumnInfo(name = Columns.nextPage)
        var nextPage: Int? = null,
    )

    companion object Contract {
        const val tableName = "posts"

        object Columns {
            const val postId = "post_id"
            const val userId = "user_id"
            const val title = "title"
            const val text = "text"
            const val date = "date"
            const val edited = "isEdited"
            const val prevPage = "prev_page"
            const val nextPage = "next_page"
        }
    }
}