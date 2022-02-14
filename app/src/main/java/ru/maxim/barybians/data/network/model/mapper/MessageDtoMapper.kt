package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.mapper.AttachmentDtoMapper
import ru.maxim.barybians.domain.model.Message
import java.util.*
import javax.inject.Inject

class MessageDtoMapper @Inject constructor(
    private val attachmentDtoMapper: AttachmentDtoMapper
) : DomainMapper<MessageDto, Message>() {

    override suspend fun toDomainModel(model: MessageDto): Message {
        return Message(
            messageId = model.messageId,
            senderId = model.senderId,
            receiverId = model.receiverId,
            text = model.text,
            date = Date(model.date * 1000),
            isUnread = model.unread == 1,
            attachments = attachmentDtoMapper.toDomainModelList(model.attachments ?: listOf())
        )
    }

    override suspend fun fromDomainModel(domainModel: Message): MessageDto {
        return MessageDto(
            messageId = domainModel.messageId,
            senderId = domainModel.senderId,
            receiverId = domainModel.receiverId,
            text = domainModel.text,
            date = domainModel.date.time / 1000,
            unread = if (domainModel.isUnread) 1 else 0,
        )
    }
}