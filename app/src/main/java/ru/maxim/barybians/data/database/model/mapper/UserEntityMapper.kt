package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.UserEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Gender
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.domain.model.UserRole
import java.util.*
import javax.inject.Inject

class UserEntityMapper @Inject constructor() : DomainMapper<UserEntity, User>() {

    override suspend fun toDomainModel(model: UserEntity): User =
        User(
            userId = model.userId,
            firstName = model.firstName,
            lastName = model.lastName,
            photo = model.photo,
            status = model.status,
            birthDate = Date(model.birthDate),
            gender = Gender.values().firstOrNull { it.genderId == model.gender } ?: Gender.Male,
            lastVisit = Date(model.lastVisit),
            role = UserRole.values().firstOrNull { it.roleId == model.roleId } ?: UserRole.Unverified
        )

    override suspend fun fromDomainModel(domainModel: User): UserEntity =
        UserEntity(
            userId = domainModel.userId,
            firstName = domainModel.firstName,
            lastName = domainModel.lastName,
            photo = domainModel.photo,
            status = domainModel.status,
            birthDate = domainModel.birthDate.time,
            gender = domainModel.gender.genderId,
            lastVisit = domainModel.lastVisit.time,
            roleId = domainModel.role.roleId
        )
}