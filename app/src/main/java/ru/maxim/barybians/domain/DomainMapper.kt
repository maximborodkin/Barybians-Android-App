package ru.maxim.barybians.domain

abstract class DomainMapper<T, DomainModel> {

    abstract fun toDomainModel(model: T): DomainModel

    abstract fun fromDomainModel(domainModel: DomainModel): T

    open fun toDomainModelList(model: List<T>): List<DomainModel> =
        model.map { toDomainModel(it) }

    open fun fromDomainModelList(domainModel: List<DomainModel>): List<T> =
        domainModel.map { fromDomainModel(it) }
}