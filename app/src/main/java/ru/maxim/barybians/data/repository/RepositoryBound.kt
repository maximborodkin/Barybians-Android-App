package ru.maxim.barybians.data.repository

import dagger.Reusable
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.maxim.barybians.data.network.exception.*
import ru.maxim.barybians.utils.NetworkUtils
import timber.log.Timber
import java.net.HttpURLConnection.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@Reusable
class RepositoryBound @Inject constructor(private val networkUtils: NetworkUtils) {

    suspend fun <DomainType> wrapRequest(
        networkRequest: suspend () -> Response<DomainType>,
    ): DomainType {
        if (networkUtils.networkStateChangeListener.value.not()) throw NoConnectionException()

        try {
            val response = withContext(IO) { networkRequest() }
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                return responseBody
            } else {
                throw when (response.code()) {
                    HTTP_BAD_REQUEST -> BadRequestException()
                    HTTP_UNAUTHORIZED -> UnauthorizedException()
                    HTTP_FORBIDDEN -> ForbiddenException()
                    HTTP_NOT_FOUND -> NotFoundException()
                    HTTP_CLIENT_TIMEOUT, HTTP_GATEWAY_TIMEOUT -> TimeoutException()
                    in 500..599 -> ServerErrorException()
                    else -> NetworkException()
                }
            }
        } catch (e: Exception) {
            Timber.w(e)
            if (e is UnknownHostException) throw NoConnectionException()
            if (e is SocketTimeoutException) throw TimeoutException()
            throw e
        }
    }
}
