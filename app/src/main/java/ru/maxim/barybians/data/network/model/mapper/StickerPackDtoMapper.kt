package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.network.model.StickerPackDto
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.StickerPack
import javax.inject.Inject

class StickerPackDtoMapper @Inject constructor() : DomainMapper<StickerPackDto, StickerPack>() {

    override fun toDomainModel(model: StickerPackDto): StickerPack =
        StickerPack(
            name = model.name,
            pack = model.pack,
            icon = model.icon,
            amount = model.amount
        )

    override fun fromDomainModel(domainModel: StickerPack): StickerPackDto =
        StickerPackDto(
            name = domainModel.name,
            pack = domainModel.pack,
            icon = domainModel.icon,
            amount = domainModel.amount
        )
}