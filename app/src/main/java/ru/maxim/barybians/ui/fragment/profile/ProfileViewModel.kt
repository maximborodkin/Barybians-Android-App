package ru.maxim.barybians.ui.fragment.profile

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.domain.model.User

class ProfileViewModel(
    application: Application,
    private val userRepository: UserRepository,
    internal val userId: Int
) : AndroidViewModel(application) {

    val user: StateFlow<User?> = userRepository.getUserById(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _errorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    init {
        refreshUser()
    }

    fun refreshUser() = viewModelScope.launch {
        try {
            userRepository.refreshUser(userId)
        } catch (e: Exception) {
            val errorMessageRes = when (e) {
                is NoConnectionException -> R.string.no_internet_connection
                is TimeoutException -> R.string.request_timeout
                else -> R.string.an_error_occurred_while_loading_profile
            }
            _errorMessage.postValue(errorMessageRes)
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
            _errorMessage.postValue(errorMessageRes)
        }
    }

    class ProfileViewModelFactory @AssistedInject constructor(
        private val application: Application,
        private val preferencesManager: PreferencesManager,
        private val userRepository: UserRepository,
        @Assisted("userId") private val userId: Int
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(
                    application = application,
                    userRepository = userRepository,
                    userId = if (userId <= 0) preferencesManager.userId else userId
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