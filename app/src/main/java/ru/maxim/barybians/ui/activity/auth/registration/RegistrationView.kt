package ru.maxim.barybians.ui.activity.auth.registration

import androidx.annotation.StringRes
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface RegistrationView : MvpView {
    @OneExecution
    fun showError(@StringRes messageRes: Int)

    @OneExecution
    fun showUsernameExistsError()

    @OneExecution
    fun openMainActivity()
}