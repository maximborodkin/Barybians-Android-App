package ru.maxim.barybians.ui.fragment.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.repository.PostRepository
import ru.maxim.barybians.data.repository.UserRepository
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.feed.FeedViewModel

class ProfileViewModel(
    application: Application,
    postRepository: PostRepository,
    private val userRepository: UserRepository,
    val userId: Int
) : FeedViewModel(application, postRepository) {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    override fun initialLoading() {
        loadUser()
    }

    fun loadUser() = viewModelScope.launch {
        internalIsLoading.postValue(true)
        try {
            _user.emit(userRepository.getUserById(userId))
            postRepository.loadPosts(userId)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.an_error_occurred_while_loading_profile
            }
            internalMessageRes.postValue(errorMessageRes)
        } finally {
            internalIsLoading.postValue(false)
        }
    }

    fun editStatus(status: String) = viewModelScope.launch {
        try {
            userRepository.editStatus(status)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.unable_to_edit_status
            }
            internalMessageRes.postValue(errorMessageRes)
        }
    }

    class ProfileViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val postRepository: PostRepository,
        private val preferencesManager: PreferencesManager,
        private val userRepository: UserRepository,
        @Assisted("userId") private val userId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                val id = if (userId <= 0) preferencesManager.userId else userId

                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(
                    application,
                    postRepository,
                    userRepository,
                    id
                ) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("userId") userId: Int): ProfileViewModelFactory
        }
    }
}