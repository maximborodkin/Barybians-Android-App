package ru.maxim.barybians.data.network.model.response

data class ErrorResponse(
    val message: String?,
    val error: Int
)