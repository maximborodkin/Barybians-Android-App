package ru.maxim.barybians.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.maxim.barybians.data.database.BarybiansDatabase.Companion.databaseVersion
import ru.maxim.barybians.data.database.dao.*
import ru.maxim.barybians.data.database.model.*

@Database(
    entities = [
        AttachmentEntity::class,
        PostAttachmentEntity::class,
        ChatEntity::class,
        CommentEntity.CommentEntityBody::class,
        LikeEntity::class,
        MessageEntity.MessageEntityBody::class,
        PostEntity.PostEntityBody::class,
        StickerPackEntity::class,
        UserEntity::class
    ],
    version = databaseVersion,
    exportSchema = false
)
abstract class BarybiansDatabase : RoomDatabase() {

    abstract fun attachmentDao(): AttachmentDao
    abstract fun postAttachmentDao(): PostAttachmentDao
    abstract fun chatDao(): ChatDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun messageDao(): MessageDao
    abstract fun postDao(): PostDao
    abstract fun stickerPackDao(): StickerPackDao
    abstract fun userDao(): UserDao

    companion object {
        const val databaseName = "barybians-database"
        const val databaseVersion = 1
    }
}