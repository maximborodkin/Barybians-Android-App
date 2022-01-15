package ru.maxim.barybians.data.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.maxim.barybians.data.network.exception.*
import timber.log.Timber
import java.net.HttpURLConnection.*

suspend fun <DomainType> repositoryBoundResource(
    networkRequest: suspend () -> Response<DomainType>,
): DomainType {
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
        throw e
    }
}