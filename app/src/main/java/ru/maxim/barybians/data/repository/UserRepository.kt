package ru.maxim.barybians.data.repository

import ru.maxim.barybians.domain.model.User

interface UserRepository {
    suspend fun getUserById(userId: Int): User?
    suspend fun editStatus(status: String): String
    suspend fun getAllUsers(): List<User>
}