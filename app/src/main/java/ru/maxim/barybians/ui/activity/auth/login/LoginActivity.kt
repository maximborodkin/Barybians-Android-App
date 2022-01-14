package ru.maxim.barybians.ui.activity.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ActivityLoginBinding
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationActivity
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.longToast
import javax.inject.Inject

class LoginActivity : MvpAppCompatActivity(), LoginView {

    @Inject
    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter

    @ProvidePresenter
    fun providePresenter(): LoginPresenter = loginPresenter

    private val binding by viewBinding(ActivityLoginBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
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

                login(login.trim(), password.trim())
            }

            // Clear error messages after any changes
            loginLogin.doAfterTextChanged { loginMessage = null }
            loginPassword.doAfterTextChanged { passwordMessage = null }

            loginRegisterLink.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            }
        }
    }

    private fun login(login: String, password: String) =
        lifecycleScope.launch {
            loginPresenter.login(login, password)
        }

    override fun showError(@StringRes messageRes: Int) = longToast(messageRes)

    override fun showInvalidCredentialsError() {
        binding.errorMessage = getString(R.string.invalid_login_or_password)
    }

    override fun openMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}