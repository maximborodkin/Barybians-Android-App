package ru.maxim.barybians.ui.activity.auth.login

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.AuthService
import java.net.HttpURLConnection

@InjectViewState
class LoginPresenter : MvpPresenter<LoginView>(), CoroutineScope by MainScope() {

    private val authService = AuthService()

    fun login(login: String, password: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoConnection()
            return
        }
        launch {
            val response = authService.auth(login, password)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.user
                PreferencesManager.token = response.body()!!.token
                PreferencesManager.userId = user.id
                val j = PreferencesManager.userId

                PreferencesManager.userName = "${user.firstName} ${user.lastName}"
                PreferencesManager.userAvatar = "${RetrofitClient.BASE_URL}/avatars/${user.photo}"
                viewState.openMainActivity()
            } else {
                when(response.code()) {
                    HttpURLConnection.HTTP_FORBIDDEN -> viewState.showInvalidData()
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> viewState.showServerError()
                    HttpURLConnection.HTTP_CLIENT_TIMEOUT -> viewState.showNetworkError()
                    HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> viewState.showNetworkError()
                    else -> viewState.showUnknownError()
                }
            }
        }
    }
}