package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.network.model.UserDto
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.domain.model.UserRole
import java.util.*
import javax.inject.Inject

class UserDtoMapper @Inject constructor() : DomainMapper<UserDto, User>() {

    override suspend fun toDomainModel(model: UserDto): User =
        User(
            userId = model.userId,
            firstName = model.firstName,
            lastName = model.lastName,
            photo = model.photo,
            status = model.status,
            birthDate = Date(model.birthDate * 1000),
            sex = model.sex,
            lastVisit = Date(model.lastVisit * 1000),
            role = UserRole.values().firstOrNull { it.roleId == model.roleId } ?: UserRole.Unverified
        )

    override suspend fun fromDomainModel(domainModel: User): UserDto =
        UserDto(
            userId = domainModel.userId,
            firstName = domainModel.firstName,
            lastName = domainModel.lastName,
            photo = domainModel.photo,
            status = domainModel.status,
            birthDate = domainModel.birthDate.time / 1000,
            sex = domainModel.sex,
            lastVisit = domainModel.lastVisit.time / 1000,
            roleId = domainModel.role.roleId
        )
}