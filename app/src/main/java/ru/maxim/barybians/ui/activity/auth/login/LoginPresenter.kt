package ru.maxim.barybians.ui.activity.auth.login

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.InvalidCredentialsException
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.network.exception.ServerErrorException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.AuthRepository
import javax.inject.Inject

@InjectViewState
class LoginPresenter @Inject constructor(private val authRepository: AuthRepository) :
    MvpPresenter<LoginView>() {

    fun login(login: String, password: String) = presenterScope.launch {
        try {
            authRepository.authenticate(login, password)
            viewState.openMainActivity()
        } catch (e: Exception) {
            when (e) {
                is NoConnectionException -> viewState.showError(R.string.no_internet_connection)
                is InvalidCredentialsException -> viewState.showInvalidCredentialsError()
                is TimeoutException -> viewState.showError(R.string.request_timeout)
                is ServerErrorException -> viewState.showError(R.string.server_error)
                else -> viewState.showError(R.string.common_network_error)
            }
        }
    }
}