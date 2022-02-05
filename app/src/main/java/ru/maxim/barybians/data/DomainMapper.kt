package ru.maxim.barybians.data

abstract class DomainMapper<T, DomainModel> {

    abstract suspend fun toDomainModel(model: T): DomainModel

    abstract suspend fun fromDomainModel(domainModel: DomainModel): T

    suspend fun toDomainModelList(model: List<T>): List<DomainModel> =
        model.map { toDomainModel(it) }

    suspend fun fromDomainModelList(domainModel: List<DomainModel>): List<T> =
        domainModel.map { fromDomainModel(it) }
}