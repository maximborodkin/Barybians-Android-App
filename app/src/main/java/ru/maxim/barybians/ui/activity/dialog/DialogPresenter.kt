package ru.maxim.barybians.ui.activity.dialog

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.DialogService

@InjectViewState
class DialogPresenter : MvpPresenter<DialogView>(), CoroutineScope by MainScope() {

    private val dialogService = DialogService()

    fun loadMessages(userId: Int) {
        if (!RetrofitClient.isOnline()) {
            return viewState.showNoInternet()
        }
        launch {
            try {
                val loadMessagesResponse = dialogService.getMessages(userId)
                if (loadMessagesResponse.isSuccessful && loadMessagesResponse.body() != null) {
                    viewState.showMessages(loadMessagesResponse.body()!!.messages)
                } else {
                    viewState.onLoadingMessagesError()
                }
            } catch (e: Exception) {
                viewState.onLoadingMessagesError()
            }
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
}