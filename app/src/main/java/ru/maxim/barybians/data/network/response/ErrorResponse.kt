package ru.maxim.barybians.data.network.response

data class ErrorResponse(
    val message: String?,
    val error: Int
)