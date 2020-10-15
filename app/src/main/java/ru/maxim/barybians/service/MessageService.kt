package ru.maxim.barybians.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import retrofit2.Response
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Dialog
import ru.maxim.barybians.model.Message
import ru.maxim.barybians.model.response.DialogResponse
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.service.DialogService
import ru.maxim.barybians.utils.isNotNull
import java.util.ArrayList

/**
 * Indeterminate background service for receiving new messages and notify user about it
 *
 * */
class MessageService : Service() {

    private val newMessagesChannel = Channel<Deferred<Response<DialogResponse>>>()
    private var lastReceivedMessageId = 0
    private val currentUserId = PreferencesManager.userId
    private val notificationsChannelId = "MessagesNotificationsChannel"
    private var messageNotificationId = 125
    private val requestTimeout = 5000L
    private val requestsFrequency = 300L
    private val dialogService = DialogService()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createMessagesNotificationChannel()
        getLastMessageId()
    }

    private fun getLastMessageId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dialogService.getDialogsList().body()?.entrySet()?.forEach {
                    val dialogMessageId = (Gson().fromJson(it.value, Dialog::class.java)).lastMessage.id
                    if (dialogMessageId > lastReceivedMessageId) lastReceivedMessageId = dialogMessageId
                }
            } catch (ignored: Exception) {

            }
        }.invokeOnCompletion { startMessagesObserving() }
    }

    private fun startMessagesObserving() {
        CoroutineScope(Dispatchers.IO).launch {
            while (!newMessagesChannel.isClosedForReceive && lastReceivedMessageId > 0) {
                withTimeoutOrNull(requestTimeout) {
                    try {
                        val pollingResponse = dialogService.observeNewMessages(lastReceivedMessageId)
                        Log.d("MESSAGES_SERVICE", "pollingResponse: $pollingResponse")
                        if (pollingResponse.isSuccessful && pollingResponse.body().isNotNull()) {
                            Log.d("MESSAGES_SERVICE", "pollingResponse: ${pollingResponse.body()}")
                            val messages = pollingResponse.body()?.messages
                            if (messages != null) {
                                Log.d("MESSAGES_SERVICE", "Messages received: $messages, lastMessageId=${messages.last().id}")
                                lastReceivedMessageId = messages.last().id
                                CoroutineScope(Dispatchers.Main).launch {
                                    createNotification(messages)
                                }
                            }
                        }
                    } catch (ignored: Exception) {
                        Log.d("MESSAGES_SERVICE", "Error: ${ignored.localizedMessage}")
                    }
                    delay(requestsFrequency)
                }
            }
        }
    }

    private fun createNotification(messages: ArrayList<Message>) {
        Log.d("MESSAGES_SERVICE", "Created notification: ${messages.last().text}")
        val builder = NotificationCompat.Builder(this, notificationsChannelId)
            .setSmallIcon(R.drawable.ic_dialogs_list_white)
            .setContentTitle("You has a new message")
            .setContentText(messages.first().text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            notify(messageNotificationId, builder.build())
        }
    }

    private fun createMessagesNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.messages_notifications_channel_name)
            val descriptionText = getString(R.string.messages_notifications_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(notificationsChannelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        newMessagesChannel.close()
        super.onDestroy()
    }
}