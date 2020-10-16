package ru.maxim.barybians.model.response

import ru.maxim.barybians.model.Message
import ru.maxim.barybians.model.User

data class MessageNotificationResponse(
    val secondUser: User,
    val message: Message
)