package ru.maxim.barybians.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.repository.auth.AuthRepository
import ru.maxim.barybians.data.repository.auth.AuthRepositoryImpl
import ru.maxim.barybians.data.repository.chat.ChatRepository
import ru.maxim.barybians.data.repository.chat.ChatRepositoryImpl
import ru.maxim.barybians.data.repository.comment.CommentRepository
import ru.maxim.barybians.data.repository.comment.CommentRepositoryImpl
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.data.repository.like.LikeRepositoryImpl
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.data.repository.post.PostRepositoryImpl
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.data.repository.user.UserRepositoryImpl
import ru.maxim.barybians.di.NetworkModule.RepositoryBindings

@Module(includes = [RepositoryBindings::class])
object NetworkModule {

    @Reusable
    @Provides
    fun provideAuthService(retrofitClient: RetrofitClient) = retrofitClient.authService

    @Reusable
    @Provides
    fun provideUserService(retrofitClient: RetrofitClient) = retrofitClient.userService

    @Reusable
    @Provides
    fun provideChatService(retrofitClient: RetrofitClient) = retrofitClient.chatService

    @Reusable
    @Provides
    fun providePostService(retrofitClient: RetrofitClient) = retrofitClient.postService

    @Reusable
    @Provides
    fun provideCommentService(retrofitClient: RetrofitClient) = retrofitClient.commentService

    @Module
    interface RepositoryBindings {

        @Binds
        fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

        @Binds
        fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

        @Binds
        fun bindCommentRepository(commentRepositoryImpl: CommentRepositoryImpl): CommentRepository

        @Binds
        fun bindLikeRepository(likeRepositoryImpl: LikeRepositoryImpl): LikeRepository

        @Binds
        fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

        @Binds
        fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
    }
}