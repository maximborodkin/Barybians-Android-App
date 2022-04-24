package ru.maxim.barybians.di

import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.NetworkManager
import ru.maxim.barybians.data.network.service.*
import ru.maxim.barybians.data.repository.auth.AuthRepository
import ru.maxim.barybians.data.repository.auth.AuthRepositoryImpl
import ru.maxim.barybians.data.repository.chat.ChatRepository
import ru.maxim.barybians.data.repository.chat.ChatRepositoryImpl
import ru.maxim.barybians.data.repository.comment.CommentRepository
import ru.maxim.barybians.data.repository.comment.CommentRepositoryImpl
import ru.maxim.barybians.data.repository.like.LikeRepository
import ru.maxim.barybians.data.repository.like.LikeRepositoryImpl
import ru.maxim.barybians.data.repository.message.MessageRepository
import ru.maxim.barybians.data.repository.message.MessageRepositoryImpl
import ru.maxim.barybians.data.repository.post.PostRepository
import ru.maxim.barybians.data.repository.post.PostRepositoryImpl
import ru.maxim.barybians.data.repository.sticker.StickerPackPackRepositoryImpl
import ru.maxim.barybians.data.repository.sticker.StickerPackRepository
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.data.repository.user.UserRepositoryImpl
import ru.maxim.barybians.di.NetworkModule.RepositoryBindings
import javax.inject.Singleton

@Module(includes = [RepositoryBindings::class])
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(preferencesManager: PreferencesManager): OkHttpClient {
        val authorizationInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${preferencesManager.token}")
                .build()
            return@Interceptor chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor { authorizationInterceptor.intercept(it) }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkManager.REST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()
    }

    @Reusable
    @Provides
    fun provideAuthService(retrofitClient: Retrofit): AuthService {
        return retrofitClient.create(AuthService::class.java)
    }

    @Reusable
    @Provides
    fun provideChatService(retrofitClient: Retrofit): ChatService {
        return retrofitClient.create(ChatService::class.java)
    }

    @Reusable
    @Provides
    fun provideCommentService(retrofitClient: Retrofit): CommentService {
        return retrofitClient.create(CommentService::class.java)
    }

    @Reusable
    @Provides
    fun provideMessageService(retrofitClient: Retrofit): MessageService {
        return retrofitClient.create(MessageService::class.java)
    }

    @Reusable
    @Provides
    fun providePostService(retrofitClient: Retrofit): PostService {
        return retrofitClient.create(PostService::class.java)
    }

    @Reusable
    @Provides
    fun provideStickerPackService(retrofitClient: Retrofit): StickerPackService {
        return retrofitClient.create(StickerPackService::class.java)
    }

    @Reusable
    @Provides
    fun provideUserService(retrofitClient: Retrofit): UserService {
        return retrofitClient.create(UserService::class.java)
    }

    @Module
    interface RepositoryBindings {

        @Binds
        fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

        @Binds
        fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

        @Binds
        fun bindCommentRepository(commentRepositoryImpl: CommentRepositoryImpl): CommentRepository

        @Binds
        fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

        @Binds
        fun bindLikeRepository(likeRepositoryImpl: LikeRepositoryImpl): LikeRepository

        @Binds
        fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

        @Binds
        fun bindStickerPackRepository(stickerPackPackRepositoryImpl: StickerPackPackRepositoryImpl): StickerPackRepository

        @Binds
        fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
    }
}