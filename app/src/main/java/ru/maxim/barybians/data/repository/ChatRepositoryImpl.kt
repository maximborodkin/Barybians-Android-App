package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.response.ChatResponse
import ru.maxim.barybians.data.network.response.SendMessageResponse
import ru.maxim.barybians.data.network.response.SendMessageResponse.MessageResponse
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Chat
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val preferencesManager: PreferencesManager
) : ChatRepository {

    override suspend fun getChatsList(): List<Chat> =
        repositoryBoundResource(chatService::getChatsList)

    override suspend fun getMessages(interlocutorId: Int): ChatResponse =
        repositoryBoundResource { chatService.getMessages(interlocutorId) }

    override suspend fun sendMessage(interlocutorId: Int, text: String): SendMessageResponse =
        repositoryBoundResource { chatService.sendMessage(interlocutorId, text) }

}