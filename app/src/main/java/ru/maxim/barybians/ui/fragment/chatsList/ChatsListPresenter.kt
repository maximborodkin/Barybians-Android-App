package ru.maxim.barybians.ui.fragment.chatsList

import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.maxim.barybians.data.network.exception.NoConnectionException
import ru.maxim.barybians.data.repository.ChatRepository
import javax.inject.Inject

@InjectViewState
class ChatsListPresenter @Inject constructor(
    private val chatRepository: ChatRepository
) : MvpPresenter<ChatsListView>() {

    override fun onFirstViewAttach() {
        loadDialogsList()
    }

    fun loadDialogsList() = presenterScope.launch {
        viewState.showLoading()
        try {
            val dialogs = chatRepository.getChatsList()
            viewState.showChatsList(dialogs.sortedByDescending { it.lastMessage.time })
        } catch (e: Exception) {
            when (e) {
                is NoConnectionException -> viewState.showNoInternet()
                else -> viewState.showChatsListLoadError()
            }
        }
    }
}