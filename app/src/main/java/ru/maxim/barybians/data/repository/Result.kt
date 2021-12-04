package ru.maxim.barybians.data.repository

sealed class Result<T> {
    data class Loading<T>(val cache: T?) : Result<T>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val cache: T?, val exception: Throwable) : Result<T>()
}
