package ru.maxim.barybians.data.repository.user

import ru.maxim.barybians.data.network.model.mapper.UserDtoMapper
import ru.maxim.barybians.data.network.service.UserService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val repositoryBound: RepositoryBound,
    private val userDtoMapper: UserDtoMapper
) : UserRepository {

    override suspend fun getUserById(userId: Int): User? {
        val userDto = repositoryBound.wrapRequest { userService.getUser(userId) } ?: return null
        return userDtoMapper.toDomainModel(userDto)
    }

    override suspend fun editStatus(status: String): String =
        repositoryBound.wrapRequest { userService.editStatus(status) }

    override suspend fun getAllUsers(): List<User> {
        val userDto = repositoryBound.wrapRequest { userService.getAll() }
        return userDtoMapper.toDomainModelList(userDto)
    }
}