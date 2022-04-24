package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.MessageEntity
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.Message.MessageStatus
import java.util.*
import javax.inject.Inject

class MessageEntityMapper @Inject constructor(
    private val attachmentEntityMapper: AttachmentEntityMapper
) : DomainMapper<MessageEntity, Message>() {

    override fun toDomainModel(model: MessageEntity): Message =
        Message(
            messageId = model.message.messageId,
            senderId = model.message.senderId,
            receiverId = model.message.receiverId,
            text = model.message.text,
            date = Date(model.message.date),
            status = MessageStatus.values().firstOrNull { it.id == model.message.status} ?: MessageStatus.Read,
            attachments = attachmentEntityMapper.toDomainModelList(model.attachments),
        )

    override fun fromDomainModel(domainModel: Message): MessageEntity =
        MessageEntity(
            message = MessageEntity.MessageEntityBody(
                messageId = domainModel.messageId,
                senderId = domainModel.senderId,
                receiverId = domainModel.receiverId,
                text = domainModel.text,
                date = domainModel.date.time,
                status = domainModel.status.id
            ),
            attachments = attachmentEntityMapper.fromDomainModelList(domainModel.attachments)
        )
}