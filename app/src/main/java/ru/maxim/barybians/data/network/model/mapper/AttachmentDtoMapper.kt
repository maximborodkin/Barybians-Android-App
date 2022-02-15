package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.data.network.model.AttachmentDto
import ru.maxim.barybians.domain.model.Attachment
import javax.inject.Inject

class AttachmentDtoMapper @Inject constructor() : DomainMapper<AttachmentDto, Attachment>() {

    override suspend fun toDomainModel(model: AttachmentDto): Attachment =
        Attachment(
            messageId = model.messageId,
            type = model.type
        )

    override suspend fun fromDomainModel(domainModel: Attachment): AttachmentDto =
        AttachmentDto(
            messageId = domainModel.messageId,
            type = domainModel.type
        )
}