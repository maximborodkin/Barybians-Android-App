package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.domain.model.User
import javax.inject.Inject

class UserEntityMapper @Inject constructor() : DomainMapper<UserEntity, User>() {

    override suspend fun toDomainModel(model: UserEntity): User =
        User(
            id = model.userId,
            firstName = model.firstName,
            lastName = model.lastName,
            photo = model.photo,
            status = model.status,
            _birthDate = model.birthDate,
            sex = model.sex,
            _lastVisit = model.lastVisit,
            roleId = model.roleId
        )

    override suspend fun fromDomainModel(domainModel: User): UserEntity =
        UserEntity(
            userId = domainModel.id,
            firstName = domainModel.firstName,
            lastName = domainModel.lastName,
            photo = domainModel.photo,
            status = domainModel.status,
            birthDate = domainModel._birthDate,
            sex = domainModel.sex,
            lastVisit = domainModel._lastVisit,
            roleId = domainModel.roleId
        )
}