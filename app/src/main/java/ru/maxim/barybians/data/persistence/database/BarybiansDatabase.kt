package ru.maxim.barybians.data.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.maxim.barybians.data.persistence.database.BarybiansDatabase.Companion.databaseVersion
import ru.maxim.barybians.data.persistence.database.dao.*
import ru.maxim.barybians.data.persistence.database.model.*

@Database(
    entities = [
        CommentEntity::class,
        MessageAttachmentEntity::class,
        MessageEntity::class,
        PostEntity::class,
        UserEntity::class
    ],
    version = databaseVersion
)
abstract class BarybiansDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao
    abstract fun commentDao(): CommentDao
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao

    companion object {
        const val databaseVersion = 1
    }
}