package ru.maxim.barybians.data.repository.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.dao.ChatDao
import ru.maxim.barybians.data.database.dao.MessageAttachmentDao
import ru.maxim.barybians.data.database.dao.MessageDao
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.mapper.AttachmentEntityMapper
import ru.maxim.barybians.data.database.model.mapper.ChatEntityMapper
import ru.maxim.barybians.data.network.model.mapper.ChatDtoMapper
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.domain.model.Message
import java.util.*
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val userDao: UserDao,
    private val attachmentDao: AttachmentDao,
    private val messageAttachmentDao: MessageAttachmentDao,
    private val chatDtoMapper: ChatDtoMapper,
    private val chatEntityMapper: ChatEntityMapper,
    private val chatService: ChatService,
    private val repositoryBound: RepositoryBound
) : ChatRepository {

    override fun getChatsList(): Flow<List<Chat>> {
        val chatsResponse = chatDao.getChatsList()
        val chats = chatsResponse.map { chatsList ->
            chatEntityMapper.toDomainModelList(chatsList)
        }
        return chats
    }

    override suspend fun refreshChatsList() {
        val chatsListResponse = repositoryBound.wrapRequest { chatService.getChatsList() }
        val chatsList = chatDtoMapper.toDomainModelList(chatsListResponse)
        val chatsListEntities = chatEntityMapper.fromDomainModelList(chatsList)
        chatDao.save(chatsListEntities, messageDao, userDao, attachmentDao, messageAttachmentDao)
    }
}