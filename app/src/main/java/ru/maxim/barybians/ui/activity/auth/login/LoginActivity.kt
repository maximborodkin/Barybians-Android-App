package ru.maxim.barybians.ui.activity.auth.login

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R

class LoginActivity : MvpAppCompatActivity(), LoginView {

    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

    }

    override fun showNoConnection() {}

    override fun showNetworkError() {}

    override fun showServerError() {}

    override fun showInvalidData() {}

    override fun showUnknownError() {}

    override fun openMainActivity() {}
}