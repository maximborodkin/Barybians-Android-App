package ru.maxim.barybians.data.repository.chat

import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.domain.model.Message

interface ChatRepository {

    suspend fun getChatsList(): List<Chat>
    suspend fun getMessages(interlocutorId: Int): List<Message>
    suspend fun sendMessage(interlocutorId: Int, text: String): Message
}