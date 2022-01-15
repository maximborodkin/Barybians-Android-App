package ru.maxim.barybians.di

import android.content.Context
import dagger.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.repository.AuthRepository
import ru.maxim.barybians.data.repository.AuthRepositoryImpl
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationActivity
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.ui.fragment.chat.ChatFragment
import ru.maxim.barybians.ui.fragment.dialogsList.DialogsListFragment
import ru.maxim.barybians.ui.fragment.feed.FeedFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment
import ru.maxim.barybians.ui.fragment.stickerPicker.StickersPickerDialog
import ru.maxim.barybians.utils.DateFormatUtils
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(chatFragment: ChatFragment)
    fun inject(dialogsListFragment: DialogsListFragment)
    fun inject(feedFragment: FeedFragment)
    fun inject(registrationActivity: RegistrationActivity)
    fun inject(profileFragment: ProfileFragment)
    fun inject(stickersPickerDialog: StickersPickerDialog)
}

@Module(includes = [UtilModule::class, DataModule::class])
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
}

@Module
object UtilModule {

    @Reusable
    @Provides
    fun providePreferencesManager(applicationContext: Context): PreferencesManager {
        return PreferencesManager(applicationContext)
    }

    @Reusable
    @Provides
    fun provideDateFormatUtils(applicationContext: Context): DateFormatUtils {
        return DateFormatUtils(applicationContext)
    }
}

@Module(includes = [DataModuleBindings::class])
object DataModule {

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
interface DataModuleBindings {

    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}