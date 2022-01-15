package ru.maxim.barybians.ui.fragment.dialogsList

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.domain.model.Chat
import javax.inject.Inject

@InjectViewState
class DialogsListPresenter @Inject constructor(
    private val chatService: ChatService,
    private val retrofitClient: RetrofitClient
) : MvpPresenter<DialogsListView>() {

    override fun onFirstViewAttach() {
        loadDialogsList()
    }

    fun loadDialogsList() = presenterScope.launch {
        try {
            val dialogs = chatService.getChatsList()
            if (dialogs.isSuccessful && dialogs.body() != null) {
                val dialogList = ArrayList<Chat>()
                dialogs.body()?.forEach {
                    dialogList.add(it)
                }
                dialogList.sortByDescending { dialog -> dialog.lastMessage.time }
                viewState.showDialogsList(dialogList)
            } else {
                viewState.onDialogsListLoadError()
            }
        } catch (e: Exception) {
            viewState.onDialogsListLoadError()
        }
    }
}