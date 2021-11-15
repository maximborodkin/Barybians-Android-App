package ru.maxim.barybians.ui.activity.auth.registration

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.service.AuthService
import java.net.HttpURLConnection.*

@InjectViewState
class RegistrationPresenter : MvpPresenter<RegistrationView>(), CoroutineScope by MainScope() {

    private val authService: AuthService by inject(AuthService::class.java)
    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)
    private val preferencesManager: PreferencesManager by inject(PreferencesManager::class.java)

    fun register(
        firstName: String, lastName: String, birthDate: String,
        sex: Boolean, login: String, password: String
    ) {
        if (!retrofitClient.isOnline()) {
            viewState.showError(R.string.no_internet_connection)
            return
        }

        launch {
            val response = authService.register(
                firstName, lastName, birthDate,
                sex, defaultAvatarUrl, login, password
            )
            if (response.isSuccessful && response.body() != null) {
                if (response.body()?.has("message") == true && response.body()
                        ?.get("message") != null
                ) {
                    val message = response.body()?.get("message")
                    if (message?.asString == "Registration was successful!") {
                        val authResponse = authService.auth(login, password)
                        if (authResponse.isSuccessful && authResponse.body() != null) {
                            preferencesManager.token = authResponse.body()!!.token
                            preferencesManager.userId = authResponse.body()!!.user.id
                            viewState.openMainActivity()
                        } else {
                            when (response.code()) {
                                HTTP_INTERNAL_ERROR -> viewState.showError(R.string.server_error)
                                HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT ->
                                    viewState.showError(R.string.request_timeout)
                                else -> viewState.showError(R.string.common_network_error)
                            }
                        }
                    }
                }
            } else {
                when (response.code()) {
                    HTTP_INTERNAL_ERROR -> {
                        if (response.message() == usernameExistsErrorMessage) {
                            viewState.showUsernameExistsError()
                        } else {
                            viewState.showError(R.string.server_error)
                        }
                    }
                    HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT ->
                        viewState.showError(R.string.request_timeout)
                    else -> viewState.showError(R.string.common_network_error)
                }
            }
        }
    }

    companion object {
        private const val defaultAvatarUrl = "min/j.png"
        private const val usernameExistsErrorMessage = "Username already exists!"
    }
}