package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.StickerPackEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.StickerPack
import javax.inject.Inject

class StickerPackEntityMapper @Inject constructor() : DomainMapper<StickerPackEntity, StickerPack>() {

    override fun toDomainModel(model: StickerPackEntity): StickerPack =
        StickerPack(
            name = model.name,
            pack = model.pack,
            icon = model.icon,
            amount = model.amount
        )

    override fun fromDomainModel(domainModel: StickerPack): StickerPackEntity =
        StickerPackEntity(
            name = domainModel.name,
            pack = domainModel.pack,
            icon = domainModel.icon,
            amount = domainModel.amount
        )
}