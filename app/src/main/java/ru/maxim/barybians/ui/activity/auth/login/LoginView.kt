package ru.maxim.barybians.ui.activity.auth.login

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface LoginView : MvpView {

    @OneExecution
    fun showError(@StringRes messageRes: Int)

    @OneExecution
    fun showInvalidCredentialsError()

    @OneExecution
    fun openMainActivity()
}