package ru.maxim.barybians.data.repository.user

import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.domain.model.User

interface UserRepository {
    fun getUserById(userId: Int): Flow<User?>
    suspend fun refreshUser(userId: Int)
    suspend fun editStatus(status: String): String
    suspend fun getAllUsers(): List<User>
}