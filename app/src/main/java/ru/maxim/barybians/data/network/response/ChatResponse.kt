package ru.maxim.barybians.data.network.response

import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User

data class ChatResponse(
    val firstUser: User,
    val secondUser: User,
    val messages: ArrayList<Message>
)