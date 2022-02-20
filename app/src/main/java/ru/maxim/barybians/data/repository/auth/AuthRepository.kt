package ru.maxim.barybians.data.repository.auth

interface AuthRepository {
    suspend fun authenticate(login: String, password: String)

    suspend fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        gender: Boolean,
        login: String,
        password: String
    )

    fun logout()
}