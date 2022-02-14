package ru.maxim.barybians.data.network.model

data class ChatDto(
    val secondUser: UserDto,
    val lastMessage: MessageDto,
    val unreadCount: Int
)