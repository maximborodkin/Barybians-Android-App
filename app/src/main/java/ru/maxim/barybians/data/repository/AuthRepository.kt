package ru.maxim.barybians.data.repository

interface AuthRepository {
    suspend fun authenticate(login: String, password: String)

    suspend fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        sex: Boolean,
        login: String,
        password: String
    )

    fun logout()
}