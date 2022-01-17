package ru.maxim.barybians.ui.activity.auth.login

import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ActivityLoginBinding
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationActivity
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.utils.*
import javax.inject.Inject
import javax.inject.Provider

class LoginActivity : MvpAppCompatActivity(R.layout.activity_login), LoginView {

    @Inject
    lateinit var presenterProvider: Provider<LoginPresenter>

    private val loginPresenter by moxyPresenter { presenterProvider.get() }

    private val binding by viewBinding(ActivityLoginBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        with(binding) {
            loginBtn.setOnClickListener {
                val login = loginLogin.text.toString()
                val password = loginPassword.text.toString()

                loginMessage =
                    if (login.isBlank()) getString(R.string.this_field_is_required)
                    else null

                passwordMessage =
                    if (password.isBlank()) getString(R.string.this_field_is_required)
                    else null

                if (loginMessage.isNull() && passwordMessage.isNull()){
                    login(login.trim(), password.trim())
                }
            }

            // Clear error messages after any changes
            loginLogin.doAfterTextChanged { loginMessage = null }
            loginPassword.doAfterTextChanged { passwordMessage = null }

            loginRegisterLink.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            }
        }
    }

    private fun login(login: String, password: String) {
        binding.loginBtn.apply {
            setDrawableStart(R.drawable.ic_timer_animated)
            compoundDrawablesRelative.firstOrNull()?.let {
                it.setTint(currentTextColor)
                (it as? Animatable)?.start()
            }
        }
        loginPresenter.login(login, password)
    }

    override fun showError(@StringRes messageRes: Int) {
        binding.loginBtn.clearDrawables()
        longToast(messageRes)
    }

    override fun showInvalidCredentialsError() {
        binding.loginBtn.clearDrawables()
        binding.errorMessage = getString(R.string.invalid_login_or_password)
    }

    override fun openMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}