package ru.maxim.barybians.data.network.model.response

data class AuthResponse(
    val user: AuthUser?,
    val token: String?
) {
    data class AuthUser(
        val userId: Int,
        val firstName: String,
        val lastName: String,
        val photo: String?
    )
}