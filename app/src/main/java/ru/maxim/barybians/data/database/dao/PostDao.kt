package ru.maxim.barybians.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.PostEntity.Contract.Columns
import ru.maxim.barybians.data.database.model.PostEntity.PostEntityBody
import ru.maxim.barybians.data.database.model.UserEntity

@Dao
abstract class PostDao {

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} ORDER BY ${Columns.date} DESC")
    abstract fun feedPagingSource(): PagingSource<Int, PostEntity>

    @Transaction
    @Query("SELECT * FROM ${PostEntity.tableName} WHERE ${Columns.userId}=:userId ORDER BY ${Columns.date} DESC")
    abstract fun userPostsPagingSource(userId: Int): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntity: PostEntityBody)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(postEntities: List<PostEntityBody>)

    @Query("DELETE FROM ${PostEntity.tableName}")
    abstract suspend fun delete()

    @Query("DELETE FROM ${PostEntity.tableName} WHERE ${Columns.postId}=:postId")
    abstract suspend fun delete(postId: Int)

    suspend fun savePosts(postEntities: List<PostEntity>, userDao: UserDao, commentDao: CommentDao, likeDao: LikeDao) {
        val posts: List<PostEntityBody> = postEntities.map { post -> post.post }
        val authors: List<UserEntity> = postEntities.map { post -> post.author }
        val comments: List<CommentEntity> = postEntities.flatMap { post -> post.comments }
        val likedUsers: List<UserEntity> = postEntities.flatMap { post -> post.likes }
        val likes: List<LikeEntity> = postEntities.flatMap { post ->
            post.likes.map { like -> LikeEntity(postId = post.post.postId, userId = like.userId) }
        }

        userDao.insert(authors + likedUsers)
        insert(posts)
        commentDao.save(comments, userDao)
        likeDao.insert(likes)
    }
}