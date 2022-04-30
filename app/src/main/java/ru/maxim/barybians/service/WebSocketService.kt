package ru.maxim.barybians.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.NetworkManager.WS_BASE_URL
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.repository.message.MessageRepository
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.longToast
import timber.log.Timber
import javax.inject.Inject

class WebSocketService : Service() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var preferencesManager: PreferencesManager

    @Inject
    lateinit var messageRepository: MessageRepository

    override fun onCreate() {
        applicationContext.appComponent.inject(this)
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        okHttpClient.dispatcher.executorService.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val request = Request.Builder().url("$WS_BASE_URL?token=${preferencesManager.token}").build()
        val webSocketListener = MessageWebSocketListener()
        okHttpClient.newWebSocket(request, webSocketListener)

        return START_STICKY
    }

    private fun proceedMessage(text: String) = applicationScope.launch(Default) {
        try {
            val event = JsonParser.parseString(text).asJsonObject
            val eventType = event["event"].asString
            val data = event["data"].asJsonObject
            when (eventType) {
                WebSocketEventType.MessageSent.serializedName -> {
                    val messageDto = Gson().fromJson(data, MessageDto::class.java)
                    messageRepository.receiveMessage(messageDto)
                }
                WebSocketEventType.MessageRead.serializedName -> {
                    val messageDto = Gson().fromJson(data, MessageDto::class.java)
                    messageRepository.markAsRead(messageDto)
                }
                else -> Timber.w("Unknown event type $eventType")
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
            //TODO: restore WebSocket after failure
            Timber.e(t)
            if (preferencesManager.isDebug) {
                applicationScope.launch(Main) {
                    applicationContext.longToast("MessageWebSocketListener has failed with ${t.localizedMessage}")
                }
            }
        }
    }
}