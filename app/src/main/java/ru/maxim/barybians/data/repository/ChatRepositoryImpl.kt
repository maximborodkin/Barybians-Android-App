package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.domain.model.Message
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val preferencesManager: PreferencesManager,
    private val repositoryBound: RepositoryBound
) : ChatRepository {

    override suspend fun getChatsList(): List<Chat> = TODO()
//        repositoryBound.wrapRequest(chatService::getChatsList)

    override suspend fun getMessages(interlocutorId: Int): List<Message> = TODO()
//        repositoryBound.wrapRequest { chatService.getMessages(interlocutorId) }

    override suspend fun sendMessage(interlocutorId: Int, text: String): Message = TODO()
//        repositoryBound.wrapRequest { chatService.sendMessage(interlocutorId, text) }

}