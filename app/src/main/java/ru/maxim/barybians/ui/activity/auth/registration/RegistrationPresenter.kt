package ru.maxim.barybians.ui.activity.auth.registration

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.AuthService
import java.net.HttpURLConnection
import java.util.*
import kotlin.math.log

@InjectViewState
class RegistrationPresenter : MvpPresenter<RegistrationView>(), CoroutineScope by MainScope() {

    private val defaultAvatarUrl ="min/j.png"
    private val authService = AuthService()

    fun register(firstName: String, lastName: String, birthDate: Long,
                 sex: Boolean, login: String, password: String) {
        if (!RetrofitClient.isOnline()){
            viewState.showNoConnection()
            return
        }
        val birthDateCalendar = Calendar.getInstance().apply { timeInMillis = birthDate }
        val birthDateString =
            "${birthDateCalendar.get(Calendar.YEAR)}-${birthDateCalendar.get(Calendar.MONTH)}-${birthDateCalendar.get(Calendar.DAY_OF_MONTH)}"

        launch {
            val response = authService.register(firstName, lastName, birthDateString,
                if (sex) 0 else 1, defaultAvatarUrl, login, password)
            if (response.isSuccessful && response.body() != null){
                if (response.body()?.has("message") == true && response.body()?.get("message") != null){
                    val message = response.body()?.get("message")
                    if (message?.asString == "Registration was successful!") {
                        val authResponse = authService.auth(login, password)
                        if (authResponse.isSuccessful && authResponse.body() != null) {
                            PreferencesManager.token = authResponse.body()!!.token
                            PreferencesManager.userId = authResponse.body()!!.user.id
                            viewState.openMainActivity()
                        } else {
                            when (response.code()) {
                                HttpURLConnection.HTTP_INTERNAL_ERROR -> viewState.showServerError()
                                HttpURLConnection.HTTP_CLIENT_TIMEOUT -> viewState.showNetworkError()
                                HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> viewState.showNetworkError()
                                else -> viewState.showUnknownError()
                            }
                        }
                    }
                }
            } else {
                when(response.code()) {
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                        if (response.message() == "Username already exists!") viewState.showRegisteredUsername()
                        else viewState.showServerError()
                    }
                    HttpURLConnection.HTTP_CLIENT_TIMEOUT -> viewState.showNetworkError()
                    HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> viewState.showNetworkError()
                    else -> viewState.showUnknownError()
                }
            }
        }
    }
}