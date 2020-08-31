package ru.maxim.barybians.ui.fragment.dialogsList

import com.arellomobile.mvp.MvpView
import ru.maxim.barybians.model.Dialog

interface DialogsListView : MvpView {

    fun showDialogsList(dialogsList: ArrayList<Dialog>)
    fun showLoading()
    fun onDialogsListLoadError()
    fun showNoInternet()
}