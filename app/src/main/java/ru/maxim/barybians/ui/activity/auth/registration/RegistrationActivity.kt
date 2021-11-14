package ru.maxim.barybians.ui.activity.auth.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ActivityRegistrationBinding
import ru.maxim.barybians.ui.activity.main.MainActivity
import ru.maxim.barybians.utils.toast
import java.util.*

class RegistrationActivity : MvpAppCompatActivity(), RegistrationView,
    DatePickerDialog.OnDateSetListener {

    @InjectPresenter
    lateinit var registrationPresenter: RegistrationPresenter
    private val binding by viewBinding(ActivityRegistrationBinding::bind)
    private val model by viewModels<RegistrationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
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
            registrationMaleBtn.setOnClickListener { model.sex.postValue(true) }
            registrationFemaleBtn.setOnClickListener { model.sex.postValue(false) }

            registrationBtn.setOnClickListener {
                with(model) {
                    if (validateFields()) {
                        register(
                            firstName.value!!.trim(),
                            lastName.value!!.trim(),
                            birthDateString.value!!,
                            sex.value == true,
                            login.value!!.trim(),
                            password.value!!.trim()
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
    ) = registrationPresenter.register(firstName, lastName, birthDate, sex, login, password)

    override fun showError(messageRes: Int) = toast(messageRes)

    override fun showUsernameExistsError() {
        binding.registrationLoginLayout.error = getText(R.string.login_already_exists)
    }

    override fun openMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}