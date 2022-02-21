package ru.maxim.barybians.data.network.model.response

import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.UserDto
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User

data class ChatResponse(
    val firstUser: UserDto,
    val secondUser: UserDto,
    val messages: List<MessageDto>
)