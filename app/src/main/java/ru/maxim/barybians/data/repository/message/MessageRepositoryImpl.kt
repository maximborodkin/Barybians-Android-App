package ru.maxim.barybians.data.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.dao.ChatDao
import ru.maxim.barybians.data.database.dao.MessageAttachmentDao
import ru.maxim.barybians.data.database.dao.MessageDao
import ru.maxim.barybians.data.database.model.ChatEntity
import ru.maxim.barybians.data.database.model.MessageEntity.MessageEntityBody
import ru.maxim.barybians.data.database.model.mapper.MessageEntityMapper
import ru.maxim.barybians.data.network.model.AttachmentDto
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.data.network.model.mapper.MessageDtoMapper
import ru.maxim.barybians.data.network.service.MessageService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.data.repository.message.MessageRemoteMediator.MessageRemoteMediatorFactory
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.domain.model.Message.MessageStatus
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MessageRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val attachmentDao: AttachmentDao,
    private val userRepository: UserRepository,
    private val messageAttachmentDao: MessageAttachmentDao,
    private val messageService: MessageService,
    private val messageDtoMapper: MessageDtoMapper,
    private val messageEntityMapper: MessageEntityMapper,
    private val messageRemoteMediatorFactory: MessageRemoteMediatorFactory,
    private val repositoryBound: RepositoryBound
) : MessageRepository {

    override fun getMessagesPager(userId: Int) = Pager(
        config = PagingConfig(
            initialLoadSize = MessageRepository.pageSize,
            pageSize = MessageRepository.pageSize,
            prefetchDistance = MessageRepository.prefetchDistance,
            enablePlaceholders = true
        ),
        remoteMediator = messageRemoteMediatorFactory.create(userId),
        pagingSourceFactory = { messageDao.messagesPagingSource(userId) }
    )
        .flow
        .map { pagingData -> pagingData.map { entityModel -> messageEntityMapper.toDomainModel(entityModel) } }

    override fun getMessagesCount(userId: Int): Flow<Int> = messageDao.messagesCount(userId)

    override suspend fun sendMessage(uuid: String, userId: Int, text: String, parseMode: ParseMode?) = withContext(IO) {
        val temporaryMessage = MessageEntityBody(
            messageId = (Int.MIN_VALUE..0).random(),
            senderId = preferencesManager.userId,
            receiverId = userId,
            text = text,
            date = Date().time,
            status = MessageStatus.Sending.id,
            prevPage = null,
            nextPage = null
        )
        messageDao.insert(temporaryMessage)

        try {
            val sendedMessage = repositoryBound.wrapRequest {
                messageService.sendMessage(
                    uuid = uuid,
                    parseMode = parseMode?.headerValue ?: ParseMode.MD.headerValue,
                    userId = userId,
                    text = text
                )
            }
            val messageDto = MessageDto(
                messageId = sendedMessage.id,
                senderId = sendedMessage.senderId,
                receiverId = userId,
                text = sendedMessage.text,
                date = sendedMessage.utime,
                unread = if (sendedMessage.unread) 1 else 0,
                attachments = sendedMessage.attachments
            )
            val domainMessage = messageDtoMapper.toDomainModel(messageDto)
            val messageEntity = messageEntityMapper.fromDomainModel(domainMessage)
            messageDao.delete(temporaryMessage)
            messageDao.save(messageEntity, attachmentDao, messageAttachmentDao)
        } catch (e: Exception) {
            messageDao.update(temporaryMessage.copy(status = MessageStatus.Error.id))
        }
    }

    override suspend fun receiveMessage(messageDto: MessageDto) = withContext(IO) {
        val message = messageDtoMapper.toDomainModel(messageDto)
        val entityModel = messageEntityMapper.fromDomainModel(message)

        val interlocutorId =
            if (messageDto.senderId == preferencesManager.userId) messageDto.receiverId
            else messageDto.senderId
        if (userRepository.getUserById(interlocutorId).firstOrNull() == null) {
            userRepository.refreshUser(interlocutorId)
        }

        messageDao.save(entityModel, attachmentDao, messageAttachmentDao)

        val chat = chatDao.getByInterlocutorId(interlocutorId)
        if (chat != null) {
            val newChat = chat.chat.copy(
                lastMessageId = messageDto.messageId,
                unreadCount = chat.chat.unreadCount + if (message.status == MessageStatus.Unread) 1 else 0
            )
            chatDao.update(newChat)
        } else {
            val newChat = ChatEntity.ChatEntityBody(
                secondUserId = interlocutorId,
                lastMessageId = messageDto.messageId,
                unreadCount = if (message.status == MessageStatus.Unread) 1 else 0
            )
            chatDao.insert(newChat)
        }
    }

    override suspend fun markAsRead(messageDto: MessageDto) = withContext(IO) {
        val message = messageDtoMapper.toDomainModel(messageDto)
        val entityModel = messageEntityMapper.fromDomainModel(message)
        messageDao.save(entityModel, attachmentDao, messageAttachmentDao)

        val interlocutorId =
            if (messageDto.senderId == preferencesManager.userId) messageDto.receiverId
            else messageDto.senderId

        val chat = chatDao.getByInterlocutorId(interlocutorId)
        if (chat != null) {
            val newChat = chat.chat.copy(
                lastMessageId = messageDto.messageId,
                unreadCount = (chat.chat.unreadCount - 1).coerceAtLeast(0)
            )
            chatDao.update(newChat)
        }
    }

    data class SendMessageResponse(
        val id: Int,
        val senderId: Int,
        val utime: Long,
        val attachments: List<AttachmentDto>,
        val unread: Boolean,
        val text: String
    )
}