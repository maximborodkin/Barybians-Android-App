package ru.maxim.barybians.data.network.response

import ru.maxim.barybians.domain.model.User

data class RegistrationResponse(
    val message: String,
    val user: User?
) {
    companion object {
        const val successMessage = "Registration was successful!"
        const val usernameExistsErrorMessage = "Username already exists!"
    }
}