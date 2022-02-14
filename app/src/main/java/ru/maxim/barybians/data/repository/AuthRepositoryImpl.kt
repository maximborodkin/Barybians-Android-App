package ru.maxim.barybians.data.repository

import com.google.gson.Gson
import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.exception.*
import ru.maxim.barybians.data.network.model.response.ErrorResponse
import ru.maxim.barybians.data.network.model.response.RegistrationResponse
import ru.maxim.barybians.data.network.service.AuthService
import ru.maxim.barybians.utils.NetworkUtils
import ru.maxim.barybians.utils.isNotNull
import timber.log.Timber
import java.net.HttpURLConnection.*
import javax.inject.Inject

@Reusable
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val networkUtils: NetworkUtils,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun authenticate(login: String, password: String) = withContext(IO) {
        try {
            if (!networkUtils.networkStateChangeListener.value) throw NoConnectionException()

            val authResponse = authService.auth(login, password)
            val responseBody = authResponse.body()

            if (authResponse.isSuccessful && responseBody != null) {
                if (responseBody.user == null || responseBody.token.isNullOrBlank())
                    throw InvalidCredentialsException()

                with(preferencesManager) {
                    token = responseBody.token
                    userId = responseBody.user.userId
                    userName = "${responseBody.user.firstName} ${responseBody.user.lastName}"
                    userAvatar = responseBody.user.photo.orEmpty()
                }
            } else {
                throw when (authResponse.code()) {
                    HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT -> TimeoutException()
                    in 500..599 -> ServerErrorException()
                    else -> NetworkException()
                }
            }
        } catch (e: Exception) {
            Timber.tag("AuthRepository").w(e)
            throw e
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        sex: Boolean,
        login: String,
        password: String
    ) = withContext(IO) {
        try {
            if (!networkUtils.networkStateChangeListener.value) throw NoConnectionException()
            val registerResponse = authService.register(
                firstName,
                lastName,
                birthDate,
                sex,
                defaultAvatarUrl,
                login,
                password
            )
            val responseBody = registerResponse.body()

            if (registerResponse.isSuccessful && responseBody != null) {
                authenticate(login, password)
            } else {
                val errorMessage =
                    if (registerResponse.errorBody().isNotNull())
                        Gson().fromJson(
                            registerResponse.errorBody()?.charStream(),
                            ErrorResponse::class.java
                        ).message
                    else null

                throw when (registerResponse.code()) {
                    HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT -> TimeoutException()
                    HTTP_BAD_REQUEST -> BadRequestException()
                    HTTP_INTERNAL_ERROR ->
                        if (errorMessage == RegistrationResponse.usernameExistsErrorMessage) AlreadyExistsException()
                        else ServerErrorException()
                    in 501..599 -> ServerErrorException()
                    else -> NetworkException()
                }
            }
        } catch (e: Exception) {
            Timber.tag(logTag).w(e)
            throw e
        }
    }

    override fun logout() {
        preferencesManager.userId = 0
        preferencesManager.token = null
    }

    companion object {
        private const val logTag = "AuthRepository"
        private const val defaultAvatarUrl = "min/j.png"
    }
}