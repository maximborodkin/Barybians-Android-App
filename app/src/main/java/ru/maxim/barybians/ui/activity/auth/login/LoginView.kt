package ru.maxim.barybians.ui.activity.auth.login

import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun showNoConnection()
    fun showNetworkError()
    fun showServerError()
    fun showInvalidData()
    fun showUnknownError()
    fun openMainActivity()
}