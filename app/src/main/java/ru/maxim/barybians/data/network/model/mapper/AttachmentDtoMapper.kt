package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.network.model.AttachmentDto
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Attachment
import ru.maxim.barybians.domain.model.Attachment.AttachmentType
import ru.maxim.barybians.domain.model.Attachment.StyledAttachmentType
import timber.log.Timber
import javax.inject.Inject

class AttachmentDtoMapper @Inject constructor() : DomainMapper<AttachmentDto, Attachment>() {

    override fun toDomainModel(model: AttachmentDto): Attachment {
        return when (model.type) {
            AttachmentType.STYLED.messageValue -> {
                val style =
                    when (requireNotNull(model.style) { "Parameter style must not be null in STYLED attachment" }) {
                        StyledAttachmentType.BOLD.messageValue -> StyledAttachmentType.BOLD
                        StyledAttachmentType.ITALIC.messageValue -> StyledAttachmentType.ITALIC
                        StyledAttachmentType.UNDERLINE.messageValue -> StyledAttachmentType.UNDERLINE
                        StyledAttachmentType.STRIKE.messageValue -> StyledAttachmentType.STRIKE
                        else -> throw IllegalArgumentException("Unknown style ${model.style}")
                    }

                Attachment(
                    attachmentId = model.attachmentId,
                    type = AttachmentType.STYLED,
                    style = style,
                    length = model.length,
                    offset = model.offset
                )
            }
            AttachmentType.IMAGE.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = AttachmentType.IMAGE,
                    url = requireNotNull(model.url) { "Parameter url must not be null in IMAGE attachment" },
                    length = model.length,
                    offset = model.offset
                )
            }
            AttachmentType.STICKER.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = AttachmentType.STICKER,
                    url = requireNotNull(model.url) { "Parameter url must not be null in STICKER attachment" },
                    pack = requireNotNull(model.pack) { "Parameter pack must not be null in STICKER attachment" },
                    sticker = requireNotNull(model.sticker) { "Parameter sticker must not be null in STICKER attachment" },
                    length = model.length,
                    offset = model.offset
                )
            }
            AttachmentType.LINK.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = AttachmentType.LINK,
                    url = requireNotNull(model.url) { "Parameter url must not be null in STICKER attachment" },
                    image = model.image,
                    title = model.title,
                    favicon = model.favicon,
                    description = model.description,
                    length = model.length,
                    offset = model.offset
                )
            }
            AttachmentType.FILE.messageValue -> {
                Attachment(
                    attachmentId = model.attachmentId,
                    type = AttachmentType.LINK,
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

    override fun fromDomainModel(domainModel: Attachment): AttachmentDto =
        AttachmentDto(
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

    override fun toDomainModelList(model: List<AttachmentDto>): List<Attachment> {
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