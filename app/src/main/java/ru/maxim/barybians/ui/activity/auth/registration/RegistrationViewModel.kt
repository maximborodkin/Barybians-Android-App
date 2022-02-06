package ru.maxim.barybians.ui.activity.auth.registration

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.AlreadyExistsException
import ru.maxim.barybians.data.network.exception.BadRequestException
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.AuthRepository
import ru.maxim.barybians.utils.isNull
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {
    // Turning to true when the registration button was pressed
    // and to false when the user starts editing data in fields
    private val isErrorsShown = MutableLiveData(false)
    private val uiDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _errorMessageRes = MutableLiveData<Int?>()
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private val _isRegistrationSuccess = MutableLiveData(false)
    val isRegistrationSuccess: LiveData<Boolean> = _isRegistrationSuccess

    val firstName = MutableLiveData(String())
    val lastName = MutableLiveData(String())
    val birthDate = MutableLiveData(getInstance())
    val birthDateString = MediatorLiveData<String>().apply {
        addSource(birthDate) { calendar ->
            this.postValue(uiDateFormat.format(calendar.time))
            birthDateApiString.postValue(apiDateFormat.format(calendar.time))
        }
    }
    val birthDateApiString = MutableLiveData(String())

    val sex = MutableLiveData(false) // true == female, false == male
    val login = MutableLiveData(String())
    val password = MutableLiveData(String())
    val repeatPassword = MutableLiveData(String())

    val today: Calendar = getInstance().apply {
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
    }

    private val _firstNameMessage = MediatorLiveData<Int?>().apply {
        addSource(firstName) { postValue(null) }
        addSource(isErrorsShown) {
            postValue(
                when {
                    it && firstName.value.isNullOrBlank() -> R.string.this_field_is_required
                    it && firstName.value?.length ?: 0 < 3 -> R.string.must_be_at_least_3_characters
                    else -> null
                }
            )
        }
    }
    val firstNameMessage: LiveData<Int?> = _firstNameMessage

    private val _lastNameMessage = MediatorLiveData<Int?>().apply {
        addSource(lastName) { postValue(null) }
        addSource(isErrorsShown) {
            postValue(
                when {
                    it && lastName.value.isNullOrBlank() -> R.string.this_field_is_required
                    it && lastName.value?.length ?: 0 < 3 -> R.string.must_be_at_least_3_characters
                    else -> null
                }
            )
        }
    }
    val lastNameMessage: LiveData<Int?> = _lastNameMessage

    private val _birthDateMessage = MediatorLiveData<Int?>().apply {
        addSource(birthDate) { postValue(null) }
        addSource(isErrorsShown) {
            postValue(
                when {
                    it && birthDate.value?.timeInMillis ?: 0L == 0L -> R.string.this_field_is_required
                    it && birthDate.value?.timeInMillis ?: 0L > today.timeInMillis -> R.string.birth_date_after_present
                    else -> null
                }
            )
        }
    }
    val birthDateMessage: LiveData<Int?> = _birthDateMessage

    private val _loginMessage = MediatorLiveData<Int?>().apply {
        addSource(login) { postValue(null) }
        addSource(isErrorsShown) {
            postValue(
                when {
                    it && login.value.isNullOrBlank() -> R.string.this_field_is_required
                    it && login.value?.length ?: 0 < 4 -> R.string.must_be_at_least_4_characters
                    else -> null
                }
            )
        }
    }
    val loginMessage: LiveData<Int?> = _loginMessage

    private val _passwordMessage = MediatorLiveData<Int?>().apply {
        addSource(password) { postValue(null) }
        addSource(isErrorsShown) {
            postValue(
                when {
                    it && password.value.isNullOrBlank() -> R.string.this_field_is_required
                    else -> null
                }
            )
        }
    }
    val passwordMessage: LiveData<Int?> = _passwordMessage

    private val _repeatPasswordMessage = MediatorLiveData<Int?>().apply {
        addSource(repeatPassword) { postValue(null) }
        addSource(isErrorsShown) {
            postValue(
                when {
                    it && repeatPassword.value.isNullOrBlank() -> R.string.this_field_is_required
                    it && repeatPassword.value?.trim() != password.value?.trim() -> R.string.passwords_didn_t_match
                    else -> null
                }
            )
        }
    }
    val repeatPasswordMessage: LiveData<Int?> = _repeatPasswordMessage

    private fun validateFields(): Boolean {
        isErrorsShown.postValue(true)

        return firstNameMessage.value.isNull() &&
                lastNameMessage.value.isNull() &&
                birthDateMessage.value.isNull() &&
                loginMessage.value.isNull() &&
                passwordMessage.value.isNull() &&
                repeatPasswordMessage.value.isNull()
    }

    fun register() = viewModelScope.launch {
        try {
            if (validateFields()) {
                authRepository.register(
                    firstName = requireNotNull(firstName.value).trim(),
                    lastName = requireNotNull(lastName.value).trim(),
                    birthDate = requireNotNull(birthDateApiString.value),
                    sex = sex.value == true,
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