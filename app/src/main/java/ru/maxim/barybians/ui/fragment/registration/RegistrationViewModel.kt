package ru.maxim.barybians.ui.fragment.registration

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.AlreadyExistsException
import ru.maxim.barybians.data.network.exception.BadRequestException
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.auth.AuthRepository
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import javax.inject.Inject

class RegistrationViewModel private constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {
    // Turning to true when the registration button was pressed
    // and to false when the user starts editing data in fields
//    private val isErrorsShown = MutableLiveData(false)
    private val uiDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _errorMessageRes = MutableLiveData<Int?>()
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private val _isRegistrationSuccess = MutableLiveData(false)
    val isRegistrationSuccess: LiveData<Boolean> = _isRegistrationSuccess

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = MutableLiveData(false)

    val today: Calendar = getInstance().apply {
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
    }

    val firstName = MutableLiveData(String())
    val lastName = MutableLiveData(String())
    val birthDate = MutableLiveData(today)
    val birthDateString = MediatorLiveData<String>().apply {
        addSource(birthDate) { calendar ->
            postValue(uiDateFormat.format(calendar.time))
            birthDateApiString.postValue(apiDateFormat.format(calendar.time))
        }
    }
    private val birthDateApiString = MutableLiveData(String())

    val gender = MutableLiveData(false) // true is female, false is male
    val login = MutableLiveData(String())
    val password = MutableLiveData(String())
    val repeatPassword = MutableLiveData(String())

    private val _firstNameMessage = MediatorLiveData<Int?>().apply { addSource(firstName) { postValue(null) } }
    val firstNameMessage: LiveData<Int?> = _firstNameMessage

    private val _lastNameMessage = MediatorLiveData<Int?>().apply { addSource(lastName) { postValue(null) } }
    val lastNameMessage: LiveData<Int?> = _lastNameMessage

    private val _birthDateMessage = MediatorLiveData<Int?>().apply { addSource(birthDate) { postValue(null) } }
    val birthDateMessage: LiveData<Int?> = _birthDateMessage

    private val _loginMessage = MediatorLiveData<Int?>().apply { addSource(login) { postValue(null) } }
    val loginMessage: LiveData<Int?> = _loginMessage

    private val _passwordMessage = MediatorLiveData<Int?>().apply { addSource(password) { postValue(null) } }
    val passwordMessage: LiveData<Int?> = _passwordMessage

    private val _repeatPasswordMessage =
        MediatorLiveData<Int?>().apply { addSource(repeatPassword) { postValue(null) } }
    val repeatPasswordMessage: LiveData<Int?> = _repeatPasswordMessage

    private fun validateFields(): Boolean {
        if (firstName.value.isNullOrBlank()) {
            _firstNameMessage.postValue(R.string.this_field_is_required)
            return false
        } else if (firstName.value?.length ?: 0 < 3) {
            _firstNameMessage.postValue(R.string.must_be_at_least_3_characters)
            return false
        }

        if (lastName.value.isNullOrBlank()) {
            _lastNameMessage.postValue(R.string.this_field_is_required)
            return false
        } else if (firstName.value?.length ?: 0 < 3) {
            _lastNameMessage.postValue(R.string.must_be_at_least_3_characters)
            return false
        }

        if (birthDate.value?.timeInMillis ?: 0L == 0L) {
            _birthDateMessage.postValue(R.string.this_field_is_required)
            return false
        }

        if (login.value.isNullOrBlank()) {
            _loginMessage.postValue(R.string.this_field_is_required)
            return false
        } else if (login.value?.length ?: 0 < 4) {
            _loginMessage.postValue(R.string.must_be_at_least_4_characters)
            return false
        }

        if (password.value.isNullOrBlank()) {
            _passwordMessage.postValue(R.string.this_field_is_required)
            return false
        }

        if (repeatPassword.value.isNullOrBlank()) {
            _repeatPasswordMessage.postValue(R.string.this_field_is_required)
            return false
        } else if (repeatPassword.value?.trim() != password.value?.trim()) {
            _repeatPasswordMessage.postValue(R.string.passwords_didn_t_match)
            return false
        }

        return true
    }

    fun register() = viewModelScope.launch {
        try {
            if (validateFields() && isLoading.value != true) {
                _isLoading.postValue(true)
                _errorMessageRes.postValue(null)

                authRepository.register(
                    firstName = requireNotNull(firstName.value).trim(),
                    lastName = requireNotNull(lastName.value).trim(),
                    birthDate = requireNotNull(birthDateApiString.value),
                    gender = gender.value == true,
                    login = requireNotNull(login.value).trim(),
                    password = requireNotNull(password.value).trim()
                )
                _isRegistrationSuccess.postValue(true)
            }
        } catch (e: Exception) {
            when (e) {
                is NoConnectionException -> _errorMessageRes.postValue(R.string.no_internet_connection)
                is AlreadyExistsException -> _errorMessageRes.postValue(R.string.login_already_exists)
                is BadRequestException -> _errorMessageRes.postValue(R.string.invalid_registration_data)
                is TimeoutException -> _errorMessageRes.postValue(R.string.request_timeout)
                else -> _errorMessageRes.postValue(R.string.common_network_error)
            }
        } finally {
            _isLoading.postValue(false)
        }
    }

    class RegistrationViewModelFactory @Inject constructor(
        private val application: Application,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegistrationViewModel(application, authRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}