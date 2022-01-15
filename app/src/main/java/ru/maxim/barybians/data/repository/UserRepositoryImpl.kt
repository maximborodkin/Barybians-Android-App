package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.service.UserService
import ru.maxim.barybians.data.persistence.PreferencesManager
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val preferencesManager: PreferencesManager
) : UserRepository {

}