package ru.maxim.barybians.data.repository.auth

import java.io.File

interface AuthRepository {
    suspend fun authenticate(login: String, password: String)

    suspend fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        gender: Int,
        avatar: File?,
        login: String,
        password: String
    )

    fun logout()
}