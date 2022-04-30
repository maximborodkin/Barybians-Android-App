package ru.maxim.barybians.data.repository.auth

import com.google.gson.Gson
import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.mapper.UserEntityMapper
import ru.maxim.barybians.data.network.exception.*
import ru.maxim.barybians.data.network.model.response.ErrorResponse
import ru.maxim.barybians.data.network.model.response.RegistrationResponse
import ru.maxim.barybians.data.network.service.AuthService
import ru.maxim.barybians.domain.model.Gender
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.domain.model.UserRole
import ru.maxim.barybians.utils.NetworkUtils
import ru.maxim.barybians.utils.isNotNull
import timber.log.Timber
import java.io.File
import java.net.HttpURLConnection.*
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject

@Reusable
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val networkUtils: NetworkUtils,
    private val preferencesManager: PreferencesManager,
    private val userDao: UserDao,
    private val userEntityMapper: UserEntityMapper
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
                }

                val currentUser = User(
                    userId = responseBody.user.userId,
                    firstName = responseBody.user.firstName,
                    lastName = responseBody.user.lastName,
                    photo = responseBody.user.photo,
                    status = responseBody.user.status,
                    birthDate = Date(responseBody.user.birthDate),
                    gender = Gender.values().firstOrNull { it.genderId == responseBody.user.sex } ?: Gender.Male,
                    lastVisit = Date(responseBody.user.lastVisit),
                    role = UserRole.values().firstOrNull { it.roleId == responseBody.user.roleId } ?: UserRole.Unverified
                )
                val userEntity = userEntityMapper.fromDomainModel(currentUser)
                userDao.save(userEntity)
            } else {
                throw when (authResponse.code()) {
                    HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT -> TimeoutException()
                    HTTP_FORBIDDEN -> InvalidCredentialsException()
                    in 500..599 -> ServerErrorException()
                    else -> NetworkException()
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            if (e is SocketTimeoutException) throw TimeoutException()
            throw e
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        gender: Int,
        avatar: File?,
        login: String,
        password: String
    ) = withContext(IO) {
        try {
            if (!networkUtils.networkStateChangeListener.value) throw NoConnectionException()

            val avatarPart = if (avatar != null) {
                val avatarBinary = avatar.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", "avatar", avatarBinary)
            } else null

            val registerResponse = authService.register(
                firstName = MultipartBody.Part.createFormData("firstName", firstName),
                lastName = MultipartBody.Part.createFormData("lastName", lastName),
                birthDate = MultipartBody.Part.createFormData("birthDate", birthDate),
                gender = MultipartBody.Part.createFormData("sex", gender.toString()),
                photo = avatarPart,
                username = MultipartBody.Part.createFormData("username", login),
                password = MultipartBody.Part.createFormData("password", password)
            )
            val responseBody = registerResponse.body()

            if (registerResponse.isSuccessful && responseBody != null) {
                authenticate(login, password)
            } else {
                val errorMessage =
                    if (registerResponse.errorBody().isNotNull())
                        Gson().fromJson(registerResponse.errorBody()?.charStream(), ErrorResponse::class.java).message
                    else
                        null

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
            Timber.e(e)
            if (e is SocketTimeoutException) throw TimeoutException()
            throw e
        }
    }

    override fun logout() {
        preferencesManager.userId = 0
        preferencesManager.token = null
    }
}