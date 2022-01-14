package ru.maxim.barybians.ui.activity.auth.login

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import org.koin.core.component.KoinComponent
import ru.maxim.barybians.R
import ru.maxim.barybians.data.network.exception.InvalidCredentialsException
import ru.maxim.barybians.data.network.exception.ServerErrorException
import ru.maxim.barybians.data.network.exception.TimeoutException
import ru.maxim.barybians.data.repository.AuthRepository
import ru.maxim.barybians.di.DaggerAppComponent
import kotlin.coroutines.CoroutineContext

@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>(), CoroutineScope, KoinComponent {
    private val authRepository: AuthRepository by inject(AuthRepository::class.java)

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    fun login(login: String, password: String) {

        launch {
            try {
                authRepository.authenticate(login, password)
                viewState.openMainActivity()
            } catch (e: Exception) {
                when(e) {
                    is InvalidCredentialsException -> viewState.showInvalidCredentialsError()
                    is TimeoutException -> viewState.showError(R.string.request_timeout)
                    is ServerErrorException -> viewState.showError(R.string.server_error)
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