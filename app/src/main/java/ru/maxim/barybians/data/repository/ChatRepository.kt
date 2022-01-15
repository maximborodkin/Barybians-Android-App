package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.response.ChatResponse
import ru.maxim.barybians.data.network.response.SendMessageResponse
import ru.maxim.barybians.domain.model.Chat

interface ChatRepository {

    suspend fun getChatsList(): List<Chat>
    suspend fun getMessages(interlocutorId: Int): ChatResponse
    suspend fun sendMessage(interlocutorId: Int, text: String): SendMessageResponse
}