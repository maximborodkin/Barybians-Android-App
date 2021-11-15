package ru.maxim.barybians.ui.fragment.dialogsList

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.model.Chat

interface DialogsListView : MvpView {

    fun showDialogsList(dialogsList: ArrayList<Chat>)
    fun showLoading()
    fun onDialogsListLoadError()
    fun showNoInternet()
}