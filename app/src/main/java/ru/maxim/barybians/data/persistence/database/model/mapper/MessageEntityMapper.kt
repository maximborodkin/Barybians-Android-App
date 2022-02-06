package ru.maxim.barybians.data.persistence.database.model.mapper

import ru.maxim.barybians.data.DomainMapper
import ru.maxim.barybians.data.persistence.database.dao.AttachmentDao
import ru.maxim.barybians.data.persistence.database.model.MessageEntity
import ru.maxim.barybians.domain.model.Message
import javax.inject.Inject

class MessageEntityMapper @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val attachmentEntityMapper: AttachmentEntityMapper
) : DomainMapper<MessageEntity, Message>() {

    override suspend fun toDomainModel(model: MessageEntity): Message {
        val attachments = requireNotNull(attachmentDao.getByMessageId(model.messageId))
        return Message(
            id = model.messageId,
            senderId = model.senderId,
            receiverId = model.receiverId,
            text = model.text,
            _date = model.date,
            unread = model.unread,
            attachments = attachmentEntityMapper.toDomainModelList(attachments)
        )
    }

    override suspend fun fromDomainModel(domainModel: Message): MessageEntity =
        MessageEntity(
            messageId = domainModel.id,
            senderId = domainModel.senderId,
            receiverId = domainModel.receiverId,
            text = domainModel.text,
            date = domainModel._date,
            unread = domainModel.unread
        )
}