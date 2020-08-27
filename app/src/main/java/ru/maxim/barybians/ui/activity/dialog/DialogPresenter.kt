package ru.maxim.barybians.ui.activity.dialog

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.maxim.barybians.repository.remote.service.DialogService

@InjectViewState
class DialogPresenter : MvpPresenter<DialogView>() {

    private val dialogService = DialogService()

    fun loadMessages(userId: Int) {

    }
}