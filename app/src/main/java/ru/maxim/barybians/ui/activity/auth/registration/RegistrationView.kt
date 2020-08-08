package ru.maxim.barybians.ui.activity.auth.registration

import com.arellomobile.mvp.MvpView

interface RegistrationView : MvpView {
    fun showNoConnection()
    fun showNetworkError()
    fun showServerError()
    fun showRegisteredUsername()
    fun showUnknownError()
    fun openMainActivity()
}