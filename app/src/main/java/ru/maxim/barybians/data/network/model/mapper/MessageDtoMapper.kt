package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.Message.MessageStatus
import java.util.*
import javax.inject.Inject

class MessageDtoMapper @Inject constructor(
    private val attachmentDtoMapper: AttachmentDtoMapper
) : DomainMapper<MessageDto, Message>() {

    override fun toDomainModel(model: MessageDto): Message =
        Message(
            messageId = model.messageId,
            senderId = model.senderId,
            receiverId = model.receiverId,
            text = model.text,
            date = Date(model.date * 1000),
            attachments = attachmentDtoMapper.toDomainModelList(model.attachments ?: listOf()),
            status = if (model.unread == 1) MessageStatus.Unread else MessageStatus.Read
        )

    override fun fromDomainModel(domainModel: Message): MessageDto =
        MessageDto(
            messageId = domainModel.messageId,
            senderId = domainModel.senderId,
            receiverId = domainModel.receiverId,
            text = domainModel.text,
            date = domainModel.date.time / 1000,
            unread = if (domainModel.status == MessageStatus.Unread) 1 else 0,
            attachments = attachmentDtoMapper.fromDomainModelList(domainModel.attachments)
        )
}