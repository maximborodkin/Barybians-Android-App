package ru.maxim.barybians.ui.activity.auth.login

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView

interface LoginView : MvpView {
    fun showError(@StringRes messageRes: Int)
    fun showInvalidCredentialsError()
    fun openMainActivity()
}