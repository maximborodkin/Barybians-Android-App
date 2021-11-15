package ru.maxim.barybians.model

data class Chat (
    val secondUser: User,
    val lastMessage: Message,
    val unreadCount: Int
)