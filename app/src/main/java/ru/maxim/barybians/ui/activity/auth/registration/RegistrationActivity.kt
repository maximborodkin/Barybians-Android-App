package ru.maxim.barybians.ui.activity.auth.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ActivityRegistrationBinding
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class RegistrationActivity : MvpAppCompatActivity(), RegistrationView,
    DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var presenterProvider: Provider<RegistrationPresenter>

    private val registrationPresenter by moxyPresenter { presenterProvider.get() }

    private val binding by viewBinding(ActivityRegistrationBinding::bind)
    private val model by viewModels<RegistrationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registration)
        binding.lifecycleOwner = this
        supportActionBar?.hide()
        with(binding) {
            viewModel = model

            registrationBackBtn.setOnClickListener { finish() }

            registrationBirthDate.keyListener = null
            registrationBirthDate.setOnClickListener {
                DatePickerDialog(
                    this@RegistrationActivity,
                    this@RegistrationActivity,
                    model.today.get(Calendar.YEAR),
                    model.today.get(Calendar.MONTH),
                    model.today.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            registrationMaleBtn.setOnClickListener { model.sex.postValue(false) }
            registrationFemaleBtn.setOnClickListener { model.sex.postValue(true) }

            registrationBtn.setOnClickListener {
                with(model) {
                    if (validateFields()) {
                        register(
                            firstName.value?.trim().toString(),
                            lastName.value?.trim().toString(),
                            birthDateString.value.toString(),
                            sex.value == true,
                            login.value?.trim().toString(),
                            password.value?.trim().toString()
                        )
                    }
                }
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        model.birthDate.postValue(calendar)
    }

    private fun register(
        firstName: String,
        lastName: String,
        birthDate: String,
        sex: Boolean,
        login: String,
        password: String
    ) {
        lifecycleScope.launch {
            registrationPresenter.register(firstName, lastName, birthDate, sex, login, password)
        }
    }

    override fun showError(@StringRes messageRes: Int) = toast(messageRes)

    override fun showUsernameExistsError() {
        binding.registrationLoginLayout.error = getText(R.string.login_already_exists)
    }

    override fun openMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}