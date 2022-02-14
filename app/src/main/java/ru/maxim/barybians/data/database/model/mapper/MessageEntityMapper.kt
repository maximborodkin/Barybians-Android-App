package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.model.MessageEntity
import ru.maxim.barybians.domain.model.Message
import java.util.*
import javax.inject.Inject

class MessageEntityMapper @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val attachmentEntityMapper: AttachmentEntityMapper
) : DomainMapper<MessageEntity, Message>() {

    override suspend fun toDomainModel(model: MessageEntity): Message {
        return Message(
            messageId = model.message.messageId,
            senderId = model.message.senderId,
            receiverId = model.message.receiverId,
            text = model.message.text,
            date = Date(model.message.date),
            isUnread = model.message.unread == 1,
            attachments = attachmentEntityMapper.toDomainModelList(model.attachments)
        )
    }

    override suspend fun fromDomainModel(domainModel: Message): MessageEntity =
        MessageEntity(
            message = MessageEntity.MessageEntityBody(
                messageId = domainModel.messageId,
                senderId = domainModel.senderId,
                receiverId = domainModel.receiverId,
                text = domainModel.text,
                date = domainModel.date.time,
                unread = if (domainModel.isUnread) 1 else 0,
            ),
            attachments = attachmentEntityMapper.fromDomainModelList(domainModel.attachments)
        )
}