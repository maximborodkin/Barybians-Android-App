package ru.maxim.barybians.data.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.maxim.barybians.data.network.exception.*
import timber.log.Timber
import java.net.HttpURLConnection.*

fun <DomainType, DtoType> repositoryBoundResource(
    databaseQuery: suspend () -> Flow<DomainType>,
    networkRequest: suspend () -> Response<DtoType>,
    cacheResponse: suspend (DtoType) -> Unit
) = flow {
    val cache = withContext(IO) { databaseQuery().firstOrNull() }
    emit(Result.Loading(cache))

    try {
        val response = withContext(IO) { networkRequest() }
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            withContext(IO) { cacheResponse(responseBody) }
            databaseQuery().map { Result.Success(it) }
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
        emit(Result.Error(cache, e))
    }
}