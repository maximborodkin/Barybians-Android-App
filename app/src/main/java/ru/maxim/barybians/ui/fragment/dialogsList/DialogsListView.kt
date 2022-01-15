package ru.maxim.barybians.ui.fragment.dialogsList

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.maxim.barybians.domain.model.Chat

interface DialogsListView : MvpView {

    @AddToEnd
    fun showDialogsList(dialogsList: ArrayList<Chat>)

    @AddToEndSingle
    fun showLoading()

    @OneExecution
    fun onDialogsListLoadError()

    @AddToEndSingle
    fun showNoInternet()
}