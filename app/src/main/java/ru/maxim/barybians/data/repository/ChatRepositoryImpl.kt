package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.response.ChatResponse
import ru.maxim.barybians.data.network.response.SendMessageResponse
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.domain.model.Chat
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val preferencesManager: PreferencesManager,
    private val repositoryBound: RepositoryBound
) : ChatRepository {

    override suspend fun getChatsList(): List<Chat> =
        repositoryBound.wrapRequest(chatService::getChatsList)

    override suspend fun getMessages(interlocutorId: Int): ChatResponse =
        repositoryBound.wrapRequest { chatService.getMessages(interlocutorId) }

    override suspend fun sendMessage(interlocutorId: Int, text: String): SendMessageResponse =
        repositoryBound.wrapRequest { chatService.sendMessage(interlocutorId, text) }

}