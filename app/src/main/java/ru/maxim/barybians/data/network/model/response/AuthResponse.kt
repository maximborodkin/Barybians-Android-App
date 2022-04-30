package ru.maxim.barybians.data.network.model.response

import ru.maxim.barybians.data.network.model.UserDto

data class AuthResponse(
    val user: UserDto?,
    val token: String?
)