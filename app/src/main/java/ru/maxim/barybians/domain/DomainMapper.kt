package ru.maxim.barybians.domain

abstract class DomainMapper<T, DomainModel> {

    abstract suspend fun toDomainModel(model: T): DomainModel

    abstract suspend fun fromDomainModel(domainModel: DomainModel): T

    open suspend fun toDomainModelList(model: List<T>): List<DomainModel> =
        model.map { toDomainModel(it) }

    open suspend fun fromDomainModelList(domainModel: List<DomainModel>): List<T> =
        domainModel.map { fromDomainModel(it) }
}