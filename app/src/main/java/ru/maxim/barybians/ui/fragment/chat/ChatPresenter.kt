package ru.maxim.barybians.ui.fragment.chat

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import retrofit2.Response
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.model.response.ChatResponse
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.utils.isNotNull
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ChatPresenter @Inject constructor(
    private val chatService: ChatService,
    private val retrofitClient: RetrofitClient
) : MvpPresenter<ChatView>() {

    private var lastMessageId = 0
    private val pollingTimeout = 50000L
    private val pollingFrequency = 100L
    private val pollingChannel = Channel<Deferred<Response<ChatResponse>>>()

    fun loadMessages(userId: Int) = presenterScope.launch {
        try {
            val loadMessagesResponse = chatService.getMessages(userId)
            loadMessagesResponse.body()?.apply {
                if (loadMessagesResponse.isSuccessful) {
                    val messages = messages
                    lastMessageId = messages.last().messageId
//                    val interlocutor = if (firstUser.id == userId) firstUser else secondUser
//                    viewState.showMessages(messages, interlocutor)
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


    fun sendMessage(interlocutorId: Int, text: String, viewHolderId: Long) = presenterScope.launch {
        launch {
            try {
                val sendMessageResponse = chatService.sendMessage(interlocutorId, text)
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
        Timber.d("Pause observing with lastMessageId $lastMessageId")
        pollingChannel.close()
    }

    private fun startDialogObserving(interlocutorId: Int) = presenterScope.launch(IO) {
        Timber.d("Start observing with postId $interlocutorId and lastMessageId $lastMessageId")
        while (!pollingChannel.isClosedForReceive) {
            withTimeoutOrNull(pollingTimeout) {
                try {
                    val pollingResponse =
                        chatService.observeMessagesForUser(interlocutorId, lastMessageId)
                    Timber.d("pollingResponse: $pollingResponse")
                    if (pollingResponse.isSuccessful && pollingResponse.body().isNotNull()) {
                        Timber.d("pollingResponse: " + pollingResponse.body())
                        val messages = pollingResponse.body()?.messages
                        if (messages != null) {
                            Timber.d("Messages received: $messages, lastMessageId: ${messages.last().messageId}")
                            lastMessageId = messages.last().messageId
                            CoroutineScope(Dispatchers.Main).launch {
//                                viewState.onMessagesReceived(messages)
                            }
                        }
                    }
                } catch (ignored: Exception) {
                    Timber.d("Error: ${ignored.localizedMessage}")
                }
                delay(pollingFrequency)
            }
        }
    }
}