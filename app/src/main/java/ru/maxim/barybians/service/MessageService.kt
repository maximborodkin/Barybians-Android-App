package ru.maxim.barybians.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.google.gson.Gson
import kotlinx.coroutines.*
import ru.maxim.barybians.R
import ru.maxim.barybians.model.Dialog
import ru.maxim.barybians.model.response.MessageNotificationResponse
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.repository.remote.service.DialogService
import ru.maxim.barybians.ui.activity.dialog.DialogActivity
import ru.maxim.barybians.utils.isNotNull
import java.util.*

/**
 * Indeterminate background service for receiving new messages and notify user about
 * it by [NotificationManager].
 *
 * */
class MessageService : Service() {

    private val newMessages = ArrayList<MessageNotificationResponse>()
    private var lastReceivedMessageId = 0
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val notificationsChannelId = "MessagesNotificationsChannel"
    private var messageNotificationId = 125
    private val pendingIntentResultCode = 367
    private val requestTimeout = 50000L
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
            while (lastReceivedMessageId > 0) {
                withTimeoutOrNull(requestTimeout) {
                    try {
                        val pollingResponse = dialogService.observeNewMessages(lastReceivedMessageId)
                        Log.d("MESSAGES_SERVICE", "pollingResponse: $pollingResponse")
                        if (pollingResponse.isSuccessful && pollingResponse.body().isNotNull()) {
                            Log.d("MESSAGES_SERVICE", "pollingResponse: ${pollingResponse.body()}")
                            pollingResponse.body()?.entrySet()?.forEach {
                                try {
                                    val message = (Gson().fromJson(
                                        it.value,
                                        MessageNotificationResponse::class.java
                                    ))
                                    newMessages.add(message)
                                    lastReceivedMessageId = message.message.id
                                    createNotification(message)
                                } catch (e: Exception){ }
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

    private fun createNotification(message: MessageNotificationResponse) {
        // TODO("Make vibrate and sound for notification")
        // TODO("Create preferences for notifications(disable, mute, mute for time range)")
        Log.d("MESSAGES_SERVICE", "Created notification: ${newMessages.last().message.text}")
        val interlocutor = message.secondUser
        val interlocutorName = "${interlocutor.firstName} ${interlocutor.lastName}"
        val dialogIntent = Intent(this, DialogActivity::class.java).apply {
            putExtra("userId", interlocutor.id)
            putExtra("userName", interlocutorName)
            putExtra("userAvatar", interlocutor.getAvatarUrl())
        }
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(dialogIntent)
            getPendingIntent(pendingIntentResultCode, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val builder = NotificationCompat.Builder(this, notificationsChannelId)
            .setSmallIcon(R.drawable.ic_dialogs_list_white)
            .setContentTitle(interlocutorName)
            .setContentText(newMessages.last().message.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        notificationManager.notify(messageNotificationId, builder.build())
    }

    private fun createMessagesNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.messages_notifications_channel_name)
            val descriptionText = getString(R.string.messages_notifications_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationsChannelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        newMessages.clear()
        super.onDestroy()
    }

    enum class CommandType {
        START_SERVICE, STOP_SERVICE, REBOOT_SERVICE, CLEAR_NOTIFICATIONS_POOL, UPDATE_SETTINGS
    }
}