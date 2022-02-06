package ru.maxim.barybians.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.*
import kotlinx.coroutines.CoroutineScope
import ru.maxim.barybians.App
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.repository.*
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationFragment
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.ui.dialog.commentsList.CommentsListDialog
import ru.maxim.barybians.ui.dialog.likesList.LikesListDialog
import ru.maxim.barybians.ui.fragment.chat.ChatFragment
import ru.maxim.barybians.ui.fragment.chatsList.ChatsListFragment
import ru.maxim.barybians.ui.fragment.feed.FeedFragment
import ru.maxim.barybians.ui.fragment.preferences.PreferencesFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment
import ru.maxim.barybians.ui.fragment.stickerPicker.StickersPickerDialog
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun applicationContext(context: Context): Builder

        @BindsInstance
        fun applicationScope(coroutineScope: CoroutineScope): Builder

        @BindsInstance
        fun preferencesManager(preferencesManager: PreferencesManager): Builder

        fun build(): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(chatFragment: ChatFragment)
    fun inject(chatsListFragment: ChatsListFragment)
    fun inject(feedFragment: FeedFragment)
    fun inject(registrationFragment: RegistrationFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(stickersPickerDialog: StickersPickerDialog)
    fun inject(preferencesFragment: PreferencesFragment)
    fun inject(commentsListDialog: CommentsListDialog)
    fun inject(likesListDialog: LikesListDialog)
    fun inject(app: App)
}

@Module(includes = [RepositoryBindings::class, PagingSourceBindings::class])
object DataModule {

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

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
    fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

    @Binds
    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}

@Module
interface PagingSourceBindings {
//
//    @Binds
//    fun bindFeedPagingSource(feedPagingSource: FeedPagingSource): PagingSource<Int, Post>
}