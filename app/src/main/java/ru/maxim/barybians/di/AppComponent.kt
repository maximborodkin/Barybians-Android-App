package ru.maxim.barybians.di

import android.app.Application
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.ui.MainActivity
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationFragment
import ru.maxim.barybians.ui.dialog.commentsList.CommentsListDialog
import ru.maxim.barybians.ui.dialog.likesList.LikesListDialog
import ru.maxim.barybians.ui.fragment.chat.ChatFragment
import ru.maxim.barybians.ui.fragment.chatsList.ChatsListFragment
import ru.maxim.barybians.ui.fragment.feed.FeedFragment
import ru.maxim.barybians.ui.fragment.login.LoginFragment
import ru.maxim.barybians.ui.fragment.preferences.PreferencesFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment
import ru.maxim.barybians.ui.fragment.stickerPicker.StickersPickerDialog
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, UtilsModule::class])
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
    fun inject(loginFragment: LoginFragment)
    fun inject(chatFragment: ChatFragment)
    fun inject(chatsListFragment: ChatsListFragment)
    fun inject(feedFragment: FeedFragment)
    fun inject(registrationFragment: RegistrationFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(stickersPickerDialog: StickersPickerDialog)
    fun inject(preferencesFragment: PreferencesFragment)
    fun inject(commentsListDialog: CommentsListDialog)
    fun inject(likesListDialog: LikesListDialog)
}