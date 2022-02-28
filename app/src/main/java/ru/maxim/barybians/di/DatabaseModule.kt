package ru.maxim.barybians.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.maxim.barybians.data.database.BarybiansDatabase
import ru.maxim.barybians.data.database.dao.*
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
    fun provideAttachmentDao(database: BarybiansDatabase): AttachmentDao = database.attachmentDao()

    @Provides
    @Reusable
    fun provideChatDao(database: BarybiansDatabase): ChatDao = database.chatDao()

    @Provides
    @Reusable
    fun provideCommentDao(database: BarybiansDatabase): CommentDao = database.commentDao()

    @Provides
    @Reusable
    fun provideLikeDao(database: BarybiansDatabase): LikeDao = database.likeDao()

    @Provides
    @Reusable
    fun provideMessageDao(database: BarybiansDatabase): MessageDao = database.messageDao()

    @Provides
    @Reusable
    fun providePostDao(database: BarybiansDatabase): PostDao = database.postDao()

    @Provides
    @Reusable
    fun provideStickerPackDao(database: BarybiansDatabase): StickerPackDao = database.stickerPackDao()

    @Provides
    @Reusable
    fun provideUserDao(database: BarybiansDatabase): UserDao = database.userDao()
}
