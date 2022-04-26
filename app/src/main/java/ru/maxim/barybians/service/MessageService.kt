package ru.maxim.barybians.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.dao.ChatDao
import ru.maxim.barybians.data.database.dao.MessageAttachmentDao
import ru.maxim.barybians.data.database.dao.MessageDao
import ru.maxim.barybians.data.database.dao.UserDao
import ru.maxim.barybians.data.database.model.ChatEntity
import ru.maxim.barybians.data.database.model.mapper.MessageEntityMapper
import ru.maxim.barybians.data.network.NetworkManager.WS_BASE_URL
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.mapper.MessageDtoMapper
import ru.maxim.barybians.data.repository.user.UserRepository
import ru.maxim.barybians.utils.appComponent
import timber.log.Timber
import javax.inject.Inject

class MessageService : Service() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var messageDao: MessageDao

    @Inject
    lateinit var chatDao: ChatDao

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var attachmentDao: AttachmentDao

    @Inject
    lateinit var messageAttachmentDao: MessageAttachmentDao

    @Inject
    lateinit var messageEntityMapper: MessageEntityMapper

    @Inject
    lateinit var messageDaoMapper: MessageDtoMapper

    override fun onCreate() {
        applicationContext.appComponent.inject(this)
        super.onCreate()

        val request = Request.Builder().url("$WS_BASE_URL?token=${preferencesManager.token}").build()
        val webSocketListener = MessageWebSocketListener()
        okHttpClient.newWebSocket(request, webSocketListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        okHttpClient.dispatcher.executorService.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun proceedMessage(text: String) = applicationScope.launch(IO) {
        try {
            val event = JsonParser.parseString(text).asJsonObject
            val eventType = event["event"].asString
            val data = event["data"].asJsonObject
            if (eventType == "message_sended") {
                val messageDto = Gson().fromJson(data, MessageDto::class.java)
                val domainModel = messageDaoMapper.toDomainModel(messageDto)
                val entityModel = messageEntityMapper.fromDomainModel(domainModel)

                val interlocutorId =
                    if (messageDto.senderId == preferencesManager.userId) messageDto.receiverId
                    else messageDto.senderId
                if (userDao.getById(interlocutorId).firstOrNull() == null) {
                    userRepository.refreshUser(interlocutorId)
                }

                messageDao.save(entityModel, attachmentDao, messageAttachmentDao)

                val chat = chatDao.getByInterlocutorId(interlocutorId)
                if (chat != null) {
                    val newChat = chat.chat.copy(
                        lastMessageId = messageDto.messageId,
                        unreadCount = chat.chat.unreadCount + messageDto.unread
                    )
                    chatDao.update(newChat)
                } else {
                    val newChat = ChatEntity.ChatEntityBody(
                        secondUserId = interlocutorId,
                        lastMessageId = messageDto.messageId,
                        unreadCount = messageDto.unread
                    )
                    chatDao.insert(newChat)
                }
            } else if (eventType == "message_readed") {
                //TODO: create database trigger to update unread messages count by update a message
                // CREATE TRIGGER update_value AFTER UPDATE ON messages
                // BEGIN
                //      INSERT INTO chats (unread_count) VALUES
                //      (SELECT SUM(unread) FROM messages WHERE sender_id=x OR interlocutor_id=x)
                //      WHERE secondUser=x
                // END;
                val messageDto = Gson().fromJson(data, MessageDto::class.java)
                val domainModel = messageDaoMapper.toDomainModel(messageDto)
                val entityModel = messageEntityMapper.fromDomainModel(domainModel)
                messageDao.save(entityModel, attachmentDao, messageAttachmentDao)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private inner class MessageWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Timber.d("MessageWebSocketListener opened")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Timber.d("MessageWebSocketListener received $text")
            proceedMessage(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("MessageWebSocketListener closed")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.d("MessageWebSocketListener failed")
        }
    }
}