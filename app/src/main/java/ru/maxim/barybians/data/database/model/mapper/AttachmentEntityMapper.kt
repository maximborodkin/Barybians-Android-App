package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Attachment
import javax.inject.Inject

class AttachmentEntityMapper @Inject constructor() : DomainMapper<AttachmentEntity, Attachment>() {

    override fun toDomainModel(model: AttachmentEntity): Attachment =
        Attachment(
            messageId = model.messageId,
            type = model.type
        )

    override fun fromDomainModel(domainModel: Attachment): AttachmentEntity =
        AttachmentEntity(
            attachmentId = 0,
            messageId = domainModel.messageId,
            type = domainModel.type
        )
}