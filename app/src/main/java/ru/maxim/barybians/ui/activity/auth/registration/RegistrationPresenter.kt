package ru.maxim.barybians.ui.activity.auth.registration

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.BadRequestException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.network.exception.AlreadyExistsException
import ru.maxim.barybians.data.repository.AuthRepository
import kotlin.coroutines.CoroutineContext

@InjectViewState
class RegistrationPresenter : MvpPresenter<RegistrationView>(), CoroutineScope {

    private val job= Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main
    private val authRepository: AuthRepository by inject(AuthRepository::class.java)

    fun register(
        firstName: String, lastName: String, birthDate: String,
        sex: Boolean, login: String, password: String
    ) {
        launch {
            try {
                authRepository.register(firstName, lastName, birthDate, sex, login, password)
                viewState.openMainActivity()
            } catch (e: Exception) {
                when(e) {
                    is AlreadyExistsException -> viewState.showUsernameExistsError()
                    is BadRequestException -> viewState.showError(R.string.invalid_registration_data)
                    is TimeoutException -> viewState.showError(R.string.request_timeout)
                    else -> viewState.showError(R.string.common_network_error)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel("Presenter calls onDestroy")
    }
}