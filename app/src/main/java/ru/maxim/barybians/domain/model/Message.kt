package ru.maxim.barybians.domain.model

import ru.maxim.barybians.data.database.model.AttachmentEntity
import ru.maxim.barybians.data.database.model.UserEntity
import java.util.*

data class Message(
    val id: Int,
    val sender: UserEntity,
    val receiver: UserEntity,
    val text: String,
    val date: Date,
    val isUnread: Boolean,
    val attachments: List<AttachmentEntity>
)