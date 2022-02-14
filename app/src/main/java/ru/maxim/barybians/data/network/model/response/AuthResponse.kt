package ru.maxim.barybians.data.network.model.response

import ru.maxim.barybians.domain.model.User

data class AuthResponse(
    val user: User?,
    val token: String?
)