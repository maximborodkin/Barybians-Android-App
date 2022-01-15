package ru.maxim.barybians.ui.fragment.chat

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.domain.model.Message
import ru.maxim.barybians.domain.model.User

interface ChatView : MvpView {

    @AddToEnd
    fun showMessages(messages: ArrayList<Message>, interlocutor: User)

    @OneExecution
    fun onLoadingMessagesError()

    @AddToEndSingle
    fun showNoInternet()

    @AddToEnd
    fun onMessageSent(text: String, messageId: Long)

    @AddToEndSingle
    fun onMessageSendingError(messageId: Long)

    @AddToEnd
    fun onMessagesReceived(messages: ArrayList<Message>)
}