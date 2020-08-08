package ru.maxim.barybians.ui.activity.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_login.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationActivity
import ru.maxim.barybians.ui.activity.main.MainActivity

class LoginActivity : MvpAppCompatActivity(), LoginView {

    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        loginBtn.setOnClickListener {
            val login = loginLogin.text.toString()
            val password = loginPassword.text.toString()

            if (login.isBlank()) loginLoginLayout.error = getString(R.string.this_field_is_required)
            else loginLoginLayout.error = null

            if (password.isBlank()) loginPasswordLayout.error = getString(R.string.this_field_is_required)
            else loginPasswordLayout.error = null

            login(login.trim(), password.trim())
        }

        loginLogin.doAfterTextChanged {
            if (!it.isNullOrBlank()) loginLoginLayout.error = null
        }
        loginPassword.doAfterTextChanged {
            if (!it.isNullOrBlank()) loginPasswordLayout.error = null
        }

        loginRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun login(login: String, password: String) {
        if (login.isNotBlank() && password.isNotBlank())
            loginPresenter.login(login, password)
    }

    override fun showNoConnection() {
        loginMessage.text = getString(R.string.no_connection)
    }

    override fun showNetworkError() {
        loginMessage.text = getString(R.string.network_error)
    }

    override fun showServerError() {
        loginMessage.text = getString(R.string.server_error)
    }

    override fun showInvalidData() {
        loginMessage.text = getString(R.string.invalid_login_or_password)
    }

    override fun showUnknownError() {
        Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
    }

    override fun openMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}