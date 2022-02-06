package ru.maxim.barybians.ui.fragment.login

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.InvalidCredentialsException
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.AuthRepository
import javax.inject.Inject

class LoginViewModel private constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    val login = MutableLiveData(String())
    val password = MutableLiveData(String())

    private val _errorMessageRes = MutableLiveData<Int?>()
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private val _isLoginSuccess = MutableLiveData(false)
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = MutableLiveData(false)

    private val _loginMessage = MediatorLiveData<Int?>().apply {
        addSource(login) { postValue(null) }
    }
    val loginMessage: LiveData<Int?> = _loginMessage

    private val _passwordMessage = MediatorLiveData<Int?>().apply {
        addSource(password) { postValue(null) }
    }
    val passwordMessage: LiveData<Int?> = _passwordMessage

    private fun validateFields(): Boolean {
        if (login.value.isNullOrBlank()) {
            _loginMessage.postValue(R.string.this_field_is_required)
            return false
        }

        if (password.value.isNullOrBlank()) {
            _passwordMessage.postValue(R.string.this_field_is_required)
            return false
        }

        return true
    }

    fun login() = viewModelScope.launch {
        try {
            if (validateFields() && isLoading.value != true) {
                _isLoading.postValue(true)
                _errorMessageRes.postValue(null)

                authRepository.authenticate(
                    login = requireNotNull(login.value).trim(),
                    password = requireNotNull(password.value).trim()
                )
                _isLoginSuccess.postValue(true)
            }
        } catch (e: Exception) {
            when (e) {
                is InvalidCredentialsException -> _errorMessageRes.postValue(R.string.invalid_login_or_password)
                is NoConnectionException -> _errorMessageRes.postValue(R.string.no_internet_connection)
                is TimeoutException -> _errorMessageRes.postValue(R.string.request_timeout)
                else -> _errorMessageRes.postValue(R.string.common_network_error)
            }
        } finally {
            _isLoading.postValue(false)
        }
    }

    class LoginViewModelFactory @Inject constructor(
        private val application: Application,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(application, authRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}