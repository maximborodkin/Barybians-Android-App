package ru.maxim.barybians.ui.activity.auth.registration

import androidx.lifecycle.*
import ru.maxim.barybians.R
import ru.maxim.barybians.utils.isNull
import java.util.*
import java.util.Calendar.*

class RegistrationViewModel : ViewModel() {
    // Turning to true when the registration button was pressed
    // and to false when the user starts editing data in fields
    private val isErrorsShown = MutableLiveData(false)

    val firstName = MutableLiveData(String())
    val lastName = MutableLiveData(String())
    val birthDate = MutableLiveData(getInstance())
    val birthDateString = Transformations.map(birthDate) { calendar ->
        // Format for dates given by API
        "${calendar.get(YEAR)}-${calendar.get(MONTH)}-${calendar.get(DAY_OF_MONTH)}"
    }
    val sex = MutableLiveData(false) // true == female, false == male
    val login = MutableLiveData(String())
    val password = MutableLiveData(String())
    val repeatPassword = MutableLiveData(String())

    val today: Calendar = getInstance().apply {
        set(HOUR_OF_DAY, 0)
        set(MINUTE, 0)
        set(SECOND, 0)
        set(MILLISECOND, 0)
        add(HOUR_OF_DAY, 3)
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

    fun validateFields(): Boolean {
        isErrorsShown.postValue(true)

        return firstNameMessage.value.isNull() &&
                lastNameMessage.value.isNull() &&
                birthDateMessage.value.isNull() &&
                loginMessage.value.isNull() &&
                passwordMessage.value.isNull() &&
                repeatPasswordMessage.value.isNull()
    }
}