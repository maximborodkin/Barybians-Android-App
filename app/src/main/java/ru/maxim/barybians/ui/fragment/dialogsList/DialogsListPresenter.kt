package ru.maxim.barybians.ui.fragment.dialogsList

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.maxim.barybians.model.Chat
import ru.maxim.barybians.repository.remote.RetrofitClient
import ru.maxim.barybians.repository.remote.service.ChatService

@InjectViewState
class DialogsListPresenter : MvpPresenter<DialogsListView>(), CoroutineScope by MainScope() {

    private val dialogService: ChatService by inject(ChatService::class.java)
    private val retrofitClient: RetrofitClient by inject(RetrofitClient::class.java)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadDialogsList()
    }

    fun loadDialogsList() {
        if (!retrofitClient.isOnline()){
            return viewState.showNoInternet()
        }
        launch {
            try {
                val dialogs = dialogService.getChatsList()
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
}