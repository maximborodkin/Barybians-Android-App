package ru.maxim.barybians.ui.activity.auth.login

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.AuthService
import java.net.HttpURLConnection.*

@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>(), CoroutineScope by MainScope() {

    private val authService = AuthService()

    fun login(login: String, password: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showError(R.string.no_connection)
            return
        }
        launch {
            val response = authService.auth(login, password)
            CoroutineScope(Dispatchers.Main).launch {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    with(PreferencesManager) {
                        token = responseBody.token
                        userId = responseBody.user.id
                        userName = "${responseBody.user.firstName} ${responseBody.user.lastName}"
                        userAvatar = "${RetrofitClient.BASE_URL}/avatars/${responseBody.user.photo}"
                    }
                    viewState.openMainActivity()
                } else {
                    when (response.code()) {
                        HTTP_FORBIDDEN -> viewState.showError(R.string.forbidden_error)
                        HTTP_INTERNAL_ERROR -> viewState.showError(R.string.server_error)
                        HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT ->
                            viewState.showError(R.string.request_timeout)
                        else -> viewState.showError(R.string.common_network_error)
                    }
                }
            }
        }
    }
}