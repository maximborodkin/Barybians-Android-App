package ru.maxim.barybians.data

/**
 * This class represents the state of the requested data of type [T].
 * [Loading] means the request just has started. "data" field inside is cached data.
 * [Success] means the request has successful. "data" field is result of the request.
 * [Error] means some unexpected result of the request. "data" is cached data, exception
 * is reason of the error
 * */
sealed class DataState<T> {
    data class Loading<T>(val data: T?) : DataState<T>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error<T>(val data: T?, val exception: Throwable) : DataState<T>()
}