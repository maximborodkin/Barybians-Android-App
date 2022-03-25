package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Attachment
import timber.log.Timber
import javax.inject.Inject

class AttachmentEntityMapper @Inject constructor() : DomainMapper<AttachmentEntity, Attachment>() {

    override fun toDomainModel(model: AttachmentEntity): Attachment {
        return when (model.type) {
            Attachment.AttachmentType.STYLED.messageValue -> {
                val style =
                    when (requireNotNull(model.style) { "Parameter style must not be null in STYLED attachment" }) {
                        Attachment.StyledAttachmentType.BOLD.messageValue -> Attachment.StyledAttachmentType.BOLD
                        Attachment.StyledAttachmentType.ITALIC.messageValue -> Attachment.StyledAttachmentType.ITALIC
                        Attachment.StyledAttachmentType.UNDERLINE.messageValue -> Attachment.StyledAttachmentType.UNDERLINE
                        Attachment.StyledAttachmentType.STRIKE.messageValue -> Attachment.StyledAttachmentType.STRIKE
                        else -> throw IllegalArgumentException("Unknown style ${model.style}")
                    }

                Attachment(
                    attachmentId = model.attachmentId,
                    type = Attachment.AttachmentType.STYLED,
                    style = style,
                    length = model.length,
                    offset = model.offset
                )
            }
            Attachment.AttachmentType.IMAGE.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = Attachment.AttachmentType.IMAGE,
                    url = requireNotNull(model.url) { "Parameter url must not be null in IMAGE attachment" },
                    length = model.length,
                    offset = model.offset
                )
            }
            Attachment.AttachmentType.STICKER.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = Attachment.AttachmentType.STICKER,
                    url = requireNotNull(model.url) { "Parameter url must not be null in STICKER attachment" },
                    pack = requireNotNull(model.pack) { "Parameter pack must not be null in STICKER attachment" },
                    sticker = requireNotNull(model.sticker) { "Parameter sticker must not be null in STICKER attachment" },
                    length = model.length,
                    offset = model.offset
                )
            }
            Attachment.AttachmentType.LINK.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = Attachment.AttachmentType.LINK,
                    url = requireNotNull(model.url) { "Parameter url must not be null in STICKER attachment" },
                    image = model.image,
                    title = model.title,
                    favicon = model.favicon,
                    description = model.description,
                    length = model.length,
                    offset = model.offset
                )
            }
            Attachment.AttachmentType.FILE.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = Attachment.AttachmentType.LINK,
                    url = requireNotNull(model.url) { "Parameter url must not be null in FILE attachment" },
                    title = model.title,
                    fileSize = model.fileSize,
                    extension = model.extension,
                    length = model.length,
                    offset = model.offset
                )
            }
            else -> throw IllegalArgumentException("Unknown attachment type ${model.type}")
        }
    }

    override fun fromDomainModel(domainModel: Attachment): AttachmentEntity =
        AttachmentEntity(
            attachmentId = domainModel.attachmentId,
            type = domainModel.type.messageValue,
            style = domainModel.style?.messageValue,
            pack = domainModel.pack,
            sticker = domainModel.sticker,
            length = domainModel.length,
            offset = domainModel.offset,
            url = domainModel.url,
            title = domainModel.title,
            fileSize = domainModel.fileSize,
            extension = domainModel.extension,
            description = domainModel.description,
            image = domainModel.image,
            favicon = domainModel.favicon
        )

    override fun toDomainModelList(model: List<AttachmentEntity>): List<Attachment> {
        val attachments = ArrayList<Attachment>()
        model.forEach { attachment ->
            try {
                attachments.add(toDomainModel(attachment))
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
            }
        }
        return attachments
    }
}