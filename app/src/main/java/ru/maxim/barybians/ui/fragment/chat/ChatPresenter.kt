package ru.maxim.barybians.ui.fragment.chat

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Response
import ru.maxim.barybians.model.response.ChatResponse
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.ChatService
import ru.maxim.barybians.utils.isNotNull

@InjectViewState
class ChatPresenter : MvpPresenter<ChatView>(), CoroutineScope by MainScope() {

    private val dialogService: ChatService by inject(ChatService::class.java)
    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)
    private var lastMessageId = 0
    private val pollingTimeout = 50000L
    private val pollingFrequency = 100L
    private val pollingChannel = Channel<Deferred<Response<ChatResponse>>>()

    fun loadMessages(userId: Int) {
        if (!retrofitClient.isOnline()) {
            return viewState.showNoInternet()
        }
        launch {
            try {
                val loadMessagesResponse = dialogService.getMessages(userId)
                loadMessagesResponse.body()?.apply {
                    if (loadMessagesResponse.isSuccessful) {
                        val messages = messages
                        lastMessageId = messages.last().id
                        val interlocutor = if (firstUser.id == userId) firstUser else secondUser
                        viewState.showMessages(messages, interlocutor)
                    } else {
                        viewState.onLoadingMessagesError()
                    }
                }
            } catch (e: Exception) {
                viewState.onLoadingMessagesError()
            }
        }.invokeOnCompletion {
            startDialogObserving(userId)
        }
    }

    fun sendMessage(interlocutorId: Int, text: String, viewHolderId: Long) {
        if (!retrofitClient.isOnline()) {
            viewState.onMessageSendingError(viewHolderId)
            return viewState.showNoInternet()
        }
        launch {
            try {
                val sendMessageResponse = dialogService.sendMessage(interlocutorId, text)
                if (sendMessageResponse.isSuccessful && sendMessageResponse.body().isNotNull()) {
                    viewState.onMessageSent(text, viewHolderId)
                } else {
                    viewState.onMessageSendingError(viewHolderId)
                }
            } catch (e: Exception) {
                viewState.onMessageSendingError(viewHolderId)
            }
        }
    }

    fun stopObserving() {
        Log.d("MESSAGES_OBSERVING", "Pause observing with lastMessageId $lastMessageId")
        pollingChannel.close()
    }

    private fun startDialogObserving(interlocutorId: Int) {
       Log.d("MESSAGES_OBSERVING", "Start observing with id $interlocutorId and lastMessageId $lastMessageId")
       CoroutineScope(Dispatchers.IO).launch {
           while (!pollingChannel.isClosedForReceive) {
               withTimeoutOrNull(pollingTimeout) {
                   try {
                       val pollingResponse = dialogService.observeMessagesForUser(interlocutorId, lastMessageId)
                       Log.d("MESSAGES_OBSERVING", "pollingResponse: $pollingResponse")
                       if (pollingResponse.isSuccessful && pollingResponse.body().isNotNull()) {
                           Log.d("MESSAGES_OBSERVING", "pollingResponse: ${pollingResponse.body()}")
                           val messages = pollingResponse.body()?.messages
                           if (messages != null) {
                               Log.d("MESSAGES_OBSERVING", "Messages received: $messages, lastMessageId=${messages.last().id}")
                               lastMessageId = messages.last().id
                               CoroutineScope(Dispatchers.Main).launch {
                                   viewState.onMessagesReceived(messages)
                               }
                           }
                       }
                   } catch (ignored: Exception) {
                       Log.d("MESSAGES_OBSERVING", "Error: ${ignored.localizedMessage}")
                   }
                   delay(pollingFrequency)
               }
           }
       }
    }
}