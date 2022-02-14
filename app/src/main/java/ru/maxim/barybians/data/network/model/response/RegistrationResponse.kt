package ru.maxim.barybians.data.network.model.response

import ru.maxim.barybians.data.network.model.UserDto

data class RegistrationResponse(
    val message: String,
    val user: UserDto?
) {
    companion object {
        const val usernameExistsErrorMessage = "Username already exists!"
    }
}