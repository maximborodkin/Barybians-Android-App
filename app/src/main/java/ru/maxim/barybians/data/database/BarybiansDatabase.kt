package ru.maxim.barybians.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.maxim.barybians.data.database.BarybiansDatabase.Companion.databaseVersion
import ru.maxim.barybians.data.database.dao.*
import ru.maxim.barybians.data.database.model.*

@Database(
    entities = [
        AttachmentEntity::class,
        ChatEntity.ChatEntityBody::class,
        CommentAttachmentEntity::class,
        CommentEntity.CommentEntityBody::class,
        LikeEntity::class,
        MessageAttachmentEntity::class,
        MessageEntity.MessageEntityBody::class,
        PostAttachmentEntity::class,
        PostEntity.PostEntityBody::class,
        StickerPackEntity::class,
        UserEntity::class
    ],
    version = databaseVersion,
    exportSchema = false
)
abstract class BarybiansDatabase : RoomDatabase() {

    abstract fun attachmentDao(): AttachmentDao
    abstract fun chatDao(): ChatDao
    abstract fun commentAttachmentDao(): CommentAttachmentDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun messageAttachmentDao(): MessageAttachmentDao
    abstract fun messageDao(): MessageDao
    abstract fun postAttachmentDao(): PostAttachmentDao
    abstract fun postDao(): PostDao
    abstract fun stickerPackDao(): StickerPackDao
    abstract fun userDao(): UserDao

    companion object {
        const val databaseName = "barybians-database"
        const val databaseVersion = 1
    }
}