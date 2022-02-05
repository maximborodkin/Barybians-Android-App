package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.RepositoryBound
import ru.maxim.barybians.data.network.service.UserService
import ru.maxim.barybians.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val repositoryBound: RepositoryBound
) : UserRepository {

    override suspend fun getUserById(userId: Int): User? =
        repositoryBound.wrapRequest { userService.getUser(userId) }

    override suspend fun editStatus(status: String): String =
        repositoryBound.wrapRequest { userService.editStatus(status) }

    override suspend fun getAllUsers(): List<User> =
        repositoryBound.wrapRequest { userService.getAll() }
}