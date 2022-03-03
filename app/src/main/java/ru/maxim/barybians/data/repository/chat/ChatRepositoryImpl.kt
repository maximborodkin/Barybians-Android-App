package ru.maxim.barybians.data.repository.chat

import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.domain.model.Message
import java.util.*
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val preferencesManager: PreferencesManager,
    private val repositoryBound: RepositoryBound
) : ChatRepository {

    override suspend fun getChatsList(): List<Chat> = listOf()
//        repositoryBound.wrapRequest(chatService::getChatsList)

    override suspend fun getMessages(interlocutorId: Int): List<Message> = listOf()
//        repositoryBound.wrapRequest { chatService.getMessages(interlocutorId) }

    override suspend fun sendMessage(interlocutorId: Int, text: String): Message = Message(
        messageId = 0,
        senderId = 0,
        receiverId = 0,
        text = "",
        date = Date(),
        isUnread = false,
        attachments = listOf()
    )
//        repositoryBound.wrapRequest { chatService.sendMessage(interlocutorId, text) }

}