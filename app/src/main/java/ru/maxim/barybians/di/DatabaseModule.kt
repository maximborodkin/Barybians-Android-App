package ru.maxim.barybians.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.maxim.barybians.data.persistence.database.BarybiansDatabase
import ru.maxim.barybians.data.persistence.database.dao.*
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): BarybiansDatabase {
        return Room.databaseBuilder(
            context,
            BarybiansDatabase::class.java,
            BarybiansDatabase.databaseName,
        ).build()
    }

    @Provides
    @Reusable
    fun provideAttachmentDao(database: BarybiansDatabase): AttachmentDao {
        return database.attachmentDao()
    }

    @Provides
    @Reusable
    fun provideChatDao(database: BarybiansDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Reusable
    fun provideCommentDao(database: BarybiansDatabase): CommentDao {
        return database.commentDao()
    }

    @Provides
    @Reusable
    fun provideLikeDao(database: BarybiansDatabase): LikeDao {
        return database.likeDao()
    }

    @Provides
    @Reusable
    fun provideMessageDao(database: BarybiansDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Reusable
    fun providePostDao(database: BarybiansDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    @Reusable
    fun provideUserDao(database: BarybiansDatabase): UserDao {
        return database.userDao()
    }
}
