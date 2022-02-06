package ru.maxim.barybians.domain.model

data class Chat(
    val secondUser: User,
    val lastMessage: Message,
    val unreadCount: Int
)