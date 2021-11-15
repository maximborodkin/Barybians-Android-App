package ru.maxim.barybians.ui.fragment.chat

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User

interface ChatView : MvpView {

    fun showMessages(messages: ArrayList<Message>, interlocutor: User)
    fun onLoadingMessagesError()
    fun showNoInternet()
    fun onMessageSent(text: String, messageId: Long)
    fun onMessageSendingError(messageId: Long)
    fun onMessagesReceived(messages: ArrayList<Message>)
}