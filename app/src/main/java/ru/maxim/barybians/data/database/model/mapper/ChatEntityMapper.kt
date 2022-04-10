package ru.maxim.barybians.data.database.model.mapper

import ru.maxim.barybians.data.database.model.ChatEntity
import ru.maxim.barybians.data.database.model.ChatEntity.ChatEntityBody
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Chat
import javax.inject.Inject

class ChatEntityMapper @Inject constructor(
    private val userEntityMapper: UserEntityMapper,
    private val messageEntityMapper: MessageEntityMapper
) : DomainMapper<ChatEntity, Chat>() {

    override fun toDomainModel(model: ChatEntity): Chat =
        Chat(
            secondUser = userEntityMapper.toDomainModel(model.secondUser),
            lastMessage = messageEntityMapper.toDomainModel(model.lastMessage),
            unreadCount = model.chat.unreadCount
        )


    override fun fromDomainModel(domainModel: Chat): ChatEntity =
        ChatEntity(
            chat = ChatEntityBody(
                secondUserId = domainModel.secondUser.userId,
                lastMessageId = domainModel.lastMessage.messageId,
                unreadCount = domainModel.unreadCount
            ),
            secondUser = userEntityMapper.fromDomainModel(domainModel.secondUser),
            lastMessage = messageEntityMapper.fromDomainModel(domainModel.lastMessage),
        )
}