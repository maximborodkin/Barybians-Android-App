package ru.maxim.barybians.data.repository.chat

import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.domain.model.Chat

interface ChatRepository {

    fun getChatsList(): Flow<List<Chat>>
    suspend fun refreshChatsList()
}