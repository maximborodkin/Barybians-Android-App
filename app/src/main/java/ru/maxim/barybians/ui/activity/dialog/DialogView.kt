package ru.maxim.barybians.ui.activity.dialog

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.model.Message

interface DialogView : MvpView {

    fun showMessages(messages: ArrayList<Message>)
    fun onLoadingMessagesError()
    fun showNoInternet()
    fun onMessageSent(text: String, messageId: Long)
    fun onMessageSendingError(messageId: Long)
    fun onMessagesReceived(messages: ArrayList<Message>)
}