package ru.maxim.barybians.ui.activity.auth.login

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            return;
        }
        launch {
            val response = authService.auth(login, password)
            CoroutineScope(Dispatchers.Main).launch {
                Log.i("response", response.body()?.token ?: "jopa")
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body() ?: throw IllegalStateException("ISE")
                    PreferencesManager.token = authData.token
                    PreferencesManager.userId = authData.user.id
                    PreferencesManager.userName =
                        "${authData.user.firstName} ${authData.user.lastName}"
                    PreferencesManager.userAvatar =
                        "${RetrofitClient.BASE_URL}/avatars/${authData.user.photo}"
                    viewState.openMainActivity()
                } else {
                    when (response.code()) {
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
}