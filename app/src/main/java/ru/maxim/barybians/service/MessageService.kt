package ru.maxim.barybians.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.utils.isNotNull
import javax.inject.Inject

/**
 * Indeterminate background service for receiving new messages and notify user about
 * it by [NotificationManager].
 *
 * */
class MessageService : Service() {

    private val log_tag = "MESSAGE_SERVICE"
    private val newMessages = ArrayList<MessageDto>()
    private var lastReceivedMessageId = 0
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val notificationsChannelId = "MessagesNotificationsChannel"
    private var messageNotificationId = 125
    private val pendingIntentResultCode = 367
    private val requestTimeout = 50000L
    private val requestsFrequency = 300L

    @Inject
    lateinit var chatService: ChatService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    inner class LocalBinder : Binder() {
        val service: MessageService = this@MessageService
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            onTaskRemoved(intent)
            val action = intent.action
            Log.e(log_tag, "using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Log.e(log_tag, "This should never happen. No action in the received intent")
            }
        }
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Message service started", Toast.LENGTH_LONG).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Message service stopped", Toast.LENGTH_LONG).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this::class.java).also {
            it.action = Actions.START.name
            it.setPackage(packageName)
        }
        applicationContext.getSystemService(Context.ALARM_SERVICE)
        applicationContext.startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun startService() {
        if (isServiceStarted) return
        isServiceStarted = true
        //preferencesManager.serviceState = ServiceState.STARTED.name
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MessageService::lock").apply {
                    acquire(10 * 60 * 1000L /*10 minutes*/)
                }
            }
        createMessagesNotificationChannel()
        getLastMessageId()
    }

    private fun stopService() {
        Log.e(log_tag, "Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopSelf()
        } catch (e: Exception) {
            Log.e(log_tag, "Service stopped without being started: ${e.message}")
        } finally {
            isServiceStarted = false
            //preferencesManager.serviceState = ServiceState.STOPPED.name
        }
    }

    private fun getLastMessageId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                chatService.getChatsList().body()?.forEach {
                    val dialogMessageId = it.lastMessage.messageId
                    if (dialogMessageId > lastReceivedMessageId) lastReceivedMessageId = dialogMessageId
                }
            } catch (ignored: Exception) {

            }
        }.invokeOnCompletion { startMessagesObserving() }
    }

    private fun startMessagesObserving() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isServiceStarted && lastReceivedMessageId > 0) {
                withTimeoutOrNull(requestTimeout) {
                    try {
                        val pollingResponse = chatService.observeNewMessages(lastReceivedMessageId)
                        Log.d("MESSAGES_SERVICE", "pollingResponse: $pollingResponse")
                        if (pollingResponse.isSuccessful && pollingResponse.body().isNotNull()) {
                            Log.d("MESSAGES_SERVICE", "pollingResponse: ${pollingResponse.body()}")
                            pollingResponse.body()?.entrySet()?.forEach {
                                try {
//                                    val message = (Gson().fromJson(
//                                        it.value,
//                                        MessageNotificationResponse::class.java
//                                    ))
//                                    newMessages.add(message)
//                                    lastReceivedMessageId = message.message.postId
//                                    createNotification(message)
                                } catch (e: Exception) {
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

//    private fun createNotification(message: MessageDto) {
//        // TODO("Make vibrate and sound for notification")
//        // TODO("Create preferences for notifications(disable, mute, mute for date range)")
//        Log.d("MESSAGES_SERVICE", "Created notification: ${newMessages.last().message.text}")
//        val interlocutor = message.secondUser
//        val interlocutorName = "${interlocutor.firstName} ${interlocutor.lastName}"
//        val dialogIntent = Intent(this, ChatFragment::class.java).apply {
//            putExtra("userId", interlocutor.postId)
//            putExtra("userName", interlocutorName)
//            putExtra("userAvatar", interlocutor.avatarMin)
//        }
//        val pendingIntent = TaskStackBuilder.create(this).run {
//            addNextIntentWithParentStack(dialogIntent)
//            getPendingIntent(pendingIntentResultCode, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//        val builder = NotificationCompat.Builder(this, notificationsChannelId)
//            .setSmallIcon(R.drawable.ic_dialogs_list_white)
//            .setContentTitle(interlocutorName)
//            .setContentText(newMessages.last().message.text)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//
//        notificationManager.notify(messageNotificationId, builder.build())
//    }

    private fun createMessagesNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.messages_notifications_channel_name)
//            val descriptionText = getString(R.string.messages_notifications_channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(notificationsChannelId, name, importance).apply {
//                description = descriptionText
//                enableVibration(true)
//            }
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
    }
}

enum class Actions {
    START,
    STOP
}

enum class ServiceState {
    STARTED,
    STOPPED,
}
