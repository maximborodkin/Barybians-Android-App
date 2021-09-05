package ru.maxim.barybians.ui.activity.dialog

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import retrofit2.Response
import ru.maxim.barybians.model.response.DialogResponse
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.DialogService
import ru.maxim.barybians.utils.isNotNull

@InjectViewState
class DialogPresenter : MvpPresenter<DialogView>(), CoroutineScope by MainScope() {

    private val dialogService = DialogService()
    private var lastMessageId = 0
    private val pollingTimeout = 50000L
    private val pollingFrequency = 100L
    private val pollingChannel = Channel<Deferred<Response<DialogResponse>>>()

    fun loadMessages(userId: Int) {
        if (!RetrofitClient.isOnline()) {
            return viewState.showNoInternet()
        }
        launch {
            try {
                val loadMessagesResponse = dialogService.getMessages(userId)
                if (loadMessagesResponse.isSuccessful && loadMessagesResponse.body().isNotNull()) {
                    val messages = loadMessagesResponse.body()!!.messages
                    lastMessageId = messages.last().id
                    viewState.showMessages(messages)
                } else {
                    viewState.onLoadingMessagesError()
                }
            } catch (e: Exception) {
                viewState.onLoadingMessagesError()
            }
        }.invokeOnCompletion {
            startDialogObserving(userId)
        }
    }

    fun sendMessage(interlocutorId: Int, text: String, viewHolderId: Long) {
        if (!RetrofitClient.isOnline()) {
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