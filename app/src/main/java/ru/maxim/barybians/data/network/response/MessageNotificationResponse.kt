package ru.maxim.barybians.data.network.response

import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User

data class MessageNotificationResponse(
    val secondUser: User,
    val message: Message
)