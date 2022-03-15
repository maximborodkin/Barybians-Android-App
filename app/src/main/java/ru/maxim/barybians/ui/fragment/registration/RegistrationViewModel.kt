package ru.maxim.barybians.ui.fragment.registration

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.exception.AlreadyExistsException
import ru.maxim.barybians.data.network.exception.BadRequestException
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.auth.AuthRepository
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import javax.inject.Inject

class RegistrationViewModel private constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val uiDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val isDarkMode = MutableLiveData<Boolean>()

    private val _errorMessageRes = MutableLiveData<Int?>()
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private val _isRegistrationSuccess = MutableLiveData(false)
    val isRegistrationSuccess: LiveData<Boolean> = _isRegistrationSuccess

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

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
    val gender = MutableLiveData(false) // false - male, true - female
    var avatar = MutableLiveData<Bitmap?>(null)
    val avatarDimensions = MutableLiveData(String())
    val avatarSize = MutableLiveData(String())
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
        return when {
            firstName.value.isNullOrBlank() -> {
                _firstNameMessage.postValue(R.string.this_field_is_required); false
            }
            firstName.value?.length ?: 0 < 2 -> {
                _firstNameMessage.postValue(R.string.must_be_at_least_2_characters); false
            }
            firstName.value?.length ?: 0 > 20 -> {
                _firstNameMessage.postValue(R.string.must_be_not_longer_20_characters); false
            }
            lastName.value.isNullOrBlank() -> {
                _lastNameMessage.postValue(R.string.this_field_is_required); false
            }
            lastName.value?.length ?: 0 < 2 -> {
                _lastNameMessage.postValue(R.string.must_be_at_least_2_characters); false
            }
            lastName.value?.length ?: 0 > 20 -> {
                _lastNameMessage.postValue(R.string.must_be_not_longer_20_characters); false
            }
            birthDate.value?.timeInMillis ?: 0L == 0L -> {
                _birthDateMessage.postValue(R.string.this_field_is_required); false
            }
            login.value.isNullOrBlank() -> {
                _loginMessage.postValue(R.string.this_field_is_required); false
            }
            login.value?.length ?: 0 < 4 -> {
                _loginMessage.postValue(R.string.must_be_at_least_4_characters); false
            }
            login.value?.length ?: 0 > 20 -> {
                _loginMessage.postValue(R.string.must_be_not_longer_20_characters); false
            }
            password.value.isNullOrBlank() -> {
                _passwordMessage.postValue(R.string.this_field_is_required); false
            }
            password.value?.length ?: 0 < 4 -> {
                _passwordMessage.postValue(R.string.must_be_at_least_4_characters); false
            }
            password.value?.length ?: 0 > 50 -> {
                _passwordMessage.postValue(R.string.must_be_not_longer_50_characters); false
            }
            repeatPassword.value.isNullOrBlank() -> {
                _repeatPasswordMessage.postValue(R.string.this_field_is_required); false
            }
            repeatPassword.value?.trim() != password.value?.trim() -> {
                _repeatPasswordMessage.postValue(R.string.passwords_didn_t_match); false
            }
            else -> true
        }
    }

    fun register(cacheDir: File?) = viewModelScope.launch {
        try {
            if (validateFields() && isLoading.value != true) {
                _isLoading.postValue(true)
                _errorMessageRes.postValue(null)

                authRepository.register(
                    firstName = requireNotNull(firstName.value).trim(),
                    lastName = requireNotNull(lastName.value).trim(),
                    birthDate = requireNotNull(birthDateApiString.value),
                    gender = if (gender.value == true) 1 else 0,
                    avatar = withContext(IO) { createFileFromBitmap(cacheDir) },
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

    private fun createFileFromBitmap(cacheDir: File?): File? {
        if (cacheDir == null) return null
        val bitmap = avatar.value ?: return null
        return try {
            val file = File(cacheDir, "avatar_${System.currentTimeMillis()}.png")
            val byteOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream)
            val byteArray: ByteArray = byteOutputStream.toByteArray()
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()
            file
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    class RegistrationViewModelFactory @Inject constructor(
        private val application: Application,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
                return RegistrationViewModel(application, authRepository) as T
            }
            throw IllegalArgumentException("Inappropriate ViewModel class ${modelClass.simpleName}")
        }
    }
}