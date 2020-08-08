package ru.maxim.barybians.model

data class Dialog (
    val secondUser: User,
    val lastMessage: Message,
    val unreadCount: Int
)