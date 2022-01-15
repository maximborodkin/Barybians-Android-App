package ru.maxim.barybians.ui.activity.auth.registration

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.AlreadyExistsException
import ru.maxim.barybians.data.network.exception.BadRequestException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.AuthRepository
import javax.inject.Inject

@InjectViewState
class RegistrationPresenter @Inject constructor(private val authRepository: AuthRepository) :
    MvpPresenter<RegistrationView>() {

    fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        sex: Boolean,
        login: String,
        password: String
    ) = presenterScope.launch {
        try {
            authRepository.register(firstName, lastName, birthDate, sex, login, password)
            viewState.openMainActivity()
        } catch (e: Exception) {
            when (e) {
                is AlreadyExistsException -> viewState.showUsernameExistsError()
                is BadRequestException -> viewState.showError(R.string.invalid_registration_data)
                is TimeoutException -> viewState.showError(R.string.request_timeout)
                else -> viewState.showError(R.string.common_network_error)
            }
        }
    }
}