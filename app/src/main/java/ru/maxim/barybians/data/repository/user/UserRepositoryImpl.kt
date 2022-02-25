package ru.maxim.barybians.data.repository.user

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.mapper.UserEntityMapper
import ru.maxim.barybians.data.network.model.mapper.UserDtoMapper
import ru.maxim.barybians.data.network.service.UserService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao,
    private val repositoryBound: RepositoryBound,
    private val userDtoMapper: UserDtoMapper,
    private val userEntityMapper: UserEntityMapper
) : UserRepository {

    override fun getUserById(userId: Int) = userDao.getById(userId)
        .map { entity -> userEntityMapper.toDomainModel(entity ?: return@map null) }

    override suspend fun refreshUser(userId: Int) = withContext(IO) {
        val userDto = repositoryBound.wrapRequest { userService.getUser(userId) }
        if (userDto != null) {
            val user = userDtoMapper.toDomainModel(userDto)
            userDao.save(userEntityMapper.fromDomainModel(user))
        }
    }

    override suspend fun editStatus(status: String): String =
        repositoryBound.wrapRequest { userService.editStatus(status) }

    override suspend fun getAllUsers(): List<User> {
        val userDto = repositoryBound.wrapRequest { userService.getAll() }
        return userDtoMapper.toDomainModelList(userDto)
    }
}