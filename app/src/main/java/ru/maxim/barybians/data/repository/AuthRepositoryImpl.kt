package ru.maxim.barybians.data.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.exception.*
import ru.maxim.barybians.data.network.response.RegistrationResponse
import ru.maxim.barybians.data.network.service.AuthService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.User
import timber.log.Timber
import java.net.HttpURLConnection.*

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val retrofitClient: RetrofitClient,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun authenticate(login: String, password: String) = withContext(IO) {
        try {
            if (!retrofitClient.isOnline()) throw NoConnectionException()

            val authResponse = authService.auth(login, password)
            val responseBody = authResponse.body()

            if (authResponse.isSuccessful && responseBody != null) {
                if (responseBody.user == null || responseBody.token.isNullOrEmpty())
                    throw InvalidCredentialsException()

                with(preferencesManager) {
                    token = responseBody.token
                    userId = responseBody.user.id
                    userName =
                        User.getFullName(responseBody.user.firstName, responseBody.user.lastName)
                    userAvatar = responseBody.user.photo.orEmpty()
                }
            } else {
                // TODO("Realize invalid credentials case")
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
            if (!retrofitClient.isOnline()) throw NoConnectionException()

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
                if (responseBody.message == RegistrationResponse.usernameExistsErrorMessage) {
                    throw AlreadyExistsException()
                }
                authenticate(login, password)
            } else {
                throw when (registerResponse.code()) {
                    HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT -> TimeoutException()
                    HTTP_BAD_REQUEST -> BadRequestException()
                    in 500..599 -> ServerErrorException()
                    else -> NetworkException()
                }
            }
        } catch (e: Exception) {
            Timber.tag("AuthRepository").w(e)
            throw e
        }
    }

    override suspend fun logout() {
        TODO("Implement logout method")
    }

    companion object {
        private const val defaultAvatarUrl = "min/j.png"
    }
}