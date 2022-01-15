package ru.maxim.barybians.ui.fragment.chatsList

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.domain.model.Chat

interface ChatsListView : MvpView {

    @AddToEnd
    fun showChatsList(chatsList: List<Chat>)

    @AddToEndSingle
    fun showLoading()

    @OneExecution
    fun showChatsListLoadError()

    @AddToEndSingle
    fun showNoInternet()
}