package ru.maxim.barybians.ui.activity.auth.registration

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpView

interface RegistrationView : MvpView {
    fun showError(@StringRes messageRes: Int)
    fun showUsernameExistsError()
    fun openMainActivity()
}