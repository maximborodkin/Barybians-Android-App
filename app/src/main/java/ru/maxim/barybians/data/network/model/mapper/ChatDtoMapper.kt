package ru.maxim.barybians.data.network.model.mapper

import ru.maxim.barybians.data.network.model.ChatDto
import ru.maxim.barybians.domain.DomainMapper
import ru.maxim.barybians.domain.model.Chat
import javax.inject.Inject

class ChatDtoMapper @Inject constructor(
    private val userDtoMapper: UserDtoMapper,
    private val messageDtoMapper: MessageDtoMapper
) : DomainMapper<ChatDto, Chat>() {

    override fun toDomainModel(model: ChatDto): Chat =
        Chat(
            secondUser = userDtoMapper.toDomainModel(model.secondUser),
            lastMessage = messageDtoMapper.toDomainModel(model.lastMessage),
            unreadCount = model.unreadCount
        )

    override fun fromDomainModel(domainModel: Chat): ChatDto =
        ChatDto(
            secondUser = userDtoMapper.fromDomainModel(domainModel.secondUser),
            lastMessage = messageDtoMapper.fromDomainModel(domainModel.lastMessage),
            unreadCount = domainModel.unreadCount
        )
}