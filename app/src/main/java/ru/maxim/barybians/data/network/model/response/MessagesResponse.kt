package ru.maxim.barybians.data.network.model.response

import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.UserDto

data class MessagesResponse(
    val secondUser: UserDto,
    val messages: List<MessageDto>
)