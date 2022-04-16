package ru.maxim.barybians.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.dao.ChatDao
import ru.maxim.barybians.data.database.dao.MessageAttachmentDao
import ru.maxim.barybians.data.database.dao.MessageDao
import ru.maxim.barybians.data.database.model.ChatEntity
import ru.maxim.barybians.data.database.model.mapper.MessageEntityMapper
import ru.maxim.barybians.data.network.RetrofitClient.Companion.WS_URL
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.WebSocketEvent
import ru.maxim.barybians.data.network.model.mapper.MessageDtoMapper
import ru.maxim.barybians.utils.appComponent
import timber.log.Timber
import javax.inject.Inject

class MessageService : Service() {

    private val job = SupervisorJob()

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .build()

    @Inject
    lateinit var messageDao: MessageDao

    @Inject
    lateinit var chatDao: ChatDao

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

        val request = Request.Builder().url(WS_URL).build()
        val webSocketListener = MessageWebSocketListener()
        okHttpClient.newWebSocket(request, webSocketListener)
        okHttpClient.dispatcher.executorService.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun proceedMessage(text: String) = CoroutineScope(job).launch {
        try {
            val event = Gson().fromJson(text, WebSocketEvent::class.java)
            if (event.event == "message_sended") {
                (event.data as? MessageDto)?.let { messageDto ->
                    val domainModel = messageDaoMapper.toDomainModel(messageDto)
                    val entityModel = messageEntityMapper.fromDomainModel(domainModel)
                    messageDao.save(entityModel, attachmentDao, messageAttachmentDao)

                    val chat =
                        chatDao.getByInterlocutorId(messageDto.senderId) ?:
                        chatDao.getByInterlocutorId(messageDto.receiverId)

                    if (chat != null) {
                        val newChat = chat.chat.copy(lastMessageId = messageDto.messageId)
                        chatDao.update(newChat)
                    } else {
                        val secondUserId =
                            if (messageDto.senderId == preferencesManager.userId) messageDto.receiverId
                            else messageDto.senderId

                        val newChat = ChatEntity.ChatEntityBody(
                            secondUserId = secondUserId,
                            lastMessageId = messageDto.messageId,
                            unreadCount = messageDto.unread
                        )
                        chatDao.insert(newChat)
                    }
                }
            } else if (event.event == "message_readed") {
                (event.data as? MessageDto)?.let { messageDto ->
                    val domainModel = messageDaoMapper.toDomainModel(messageDto)
                    val entityModel = messageEntityMapper.fromDomainModel(domainModel)
                    messageDao.save(entityModel, attachmentDao, messageAttachmentDao)
                }
            }
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private inner class MessageWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Timber.d("MessageWebSocketListener opened")
            val tokenMessage = "token=${preferencesManager.token}"
            webSocket.send(tokenMessage)
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