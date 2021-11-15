package ru.maxim.barybians.model.response

import ru.maxim.barybians.model.Message
import ru.maxim.barybians.model.User

data class ChatResponse(
    val firstUser: User,
    val secondUser: User,
    val messages: ArrayList<Message>
)