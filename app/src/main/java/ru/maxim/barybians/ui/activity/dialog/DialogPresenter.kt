package ru.maxim.barybians.ui.activity.dialog

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
                if (sendMessageResponse.isSuccessful && sendMessageResponse.body() == "true") {
                    viewState.onMessageSent(text, viewHolderId)
                } else {
                    viewState.onMessageSendingError(viewHolderId)
                }
            } catch (e: Exception) {
                viewState.onMessageSendingError(viewHolderId)
            }
        }
    }

//    fun startDialogObserving(interlocutorId: Int) {
//        CoroutineScope(Dispatchers.IO).launch {
//            withTimeoutOrNull(pollingTimeout) {
//                while (pollingChannel.isNotNull()) {
//                    pollingChannel.send(async {
//                          dialogLongPollingService.observeMessages(interlocutorId, lastMessageId)
//                    })
//                    delay(pollingFrequency)
//                }
//            }
//        }
//        CoroutineScope(Dispatchers.IO).launch {
//            for (event in pollingChannel) {
//                val messages = event.await().body()?.messages
//                if (messages != null) {
//                    Log.i("MESSAGE_OBSERVER", "event: $event, messages: $messages")
//                    lastMessageId = messages.last().id
//                    viewState.onMessageReceived(messages)
//                }
//            }
//        }
//    }

    private fun startDialogObserving(interlocutorId: Int) {
       CoroutineScope(Dispatchers.IO).launch {
           while (pollingChannel.isNotNull()) {
               withTimeoutOrNull(pollingTimeout) {
                   try {
                       val pollingResponse =
                           dialogService.observeMessages(interlocutorId, lastMessageId)
                       if (pollingResponse.isSuccessful && pollingResponse.body().isNotNull()) {
                           val messages = pollingResponse.body()?.messages
                           if (messages != null) {
                               val lm = messages.last().id
                               lastMessageId = lm
                               CoroutineScope(Dispatchers.Main).launch {
                                   viewState.onMessageReceived(messages)
                               }
                           }
                       }
                   } catch (ignored: Exception) { }
                   delay(pollingFrequency)
               }
           }
       }
    }
}