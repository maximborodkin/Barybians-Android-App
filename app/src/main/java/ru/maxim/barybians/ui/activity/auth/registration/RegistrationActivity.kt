package ru.maxim.barybians.ui.activity.auth.registration

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_registration.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.activity.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : MvpAppCompatActivity(), RegistrationView, DatePickerDialog.OnDateSetListener {

    @InjectPresenter
    lateinit var registrationPresenter: RegistrationPresenter

    private val birthDateCalendar = Calendar.getInstance().apply { timeInMillis = 0L }
    private var sex = true // true - male, false - female
    private val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 3)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()
        registrationBackBtn.setOnClickListener {
            finish()
        }

        registrationBirthDate.keyListener = null
        registrationBirthDate.setOnClickListener {
            DatePickerDialog(this, this, today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
        }
        registrationBirthDate.doAfterTextChanged {
            if (it.isNullOrBlank()) birthDateCalendar.timeInMillis = 0L
        }

        registrationMaleBtn.setBackgroundResource(R.drawable.ic_male_selected)
        registrationFemaleBtn.setBackgroundResource(R.drawable.ic_female_unselected)
        registrationMaleBtn.setOnClickListener {
            sex = true
            registrationMaleBtn.setBackgroundResource(R.drawable.ic_male_selected)
            registrationFemaleBtn.setBackgroundResource(R.drawable.ic_female_unselected)
        }

        registrationFemaleBtn.setOnClickListener {
            sex = false
            registrationMaleBtn.setBackgroundResource(R.drawable.ic_male_unselected)
            registrationFemaleBtn.setBackgroundResource(R.drawable.ic_female_selected)
        }

        registrationFirstName.doAfterTextChanged {
            if (!it.isNullOrBlank()) registrationFirstNameLayout.error = null
        }

        registrationLastName.doAfterTextChanged {
            if (!it.isNullOrBlank()) registrationLastNameLayout.error = null
        }

        registrationBirthDate.doAfterTextChanged {
            if (birthDateCalendar.timeInMillis != 0L && birthDateCalendar.timeInMillis <= today.timeInMillis)
                registrationBirthDateLayout.error = null
        }

        registrationLogin.doAfterTextChanged {
            if (!it.isNullOrBlank()) registrationLoginLayout.error = null
        }

        registrationPassword.doAfterTextChanged {
            if (!it.isNullOrBlank()) registrationPasswordLayout.error = null
        }

        registrationPasswordRepeat.doAfterTextChanged {
            if (!it.isNullOrBlank()) registrationPasswordRepeatLayout.error = null
        }

        registrationBtn.setOnClickListener {
            val firstName = registrationFirstName.text.toString()
            val lastName = registrationLastName.text.toString()
            val birthDate = birthDateCalendar.timeInMillis
            val login = registrationLogin.text.toString()
            val password = registrationPassword.text.toString()
            val passwordRepeat = registrationPasswordRepeat.text.toString()

            when {
                firstName.isBlank() -> registrationFirstNameLayout.error = getString(R.string.this_field_is_required)
                firstName.length < 3 -> registrationFirstNameLayout.error = getString(R.string.must_be_at_least_3_characters)
                else -> registrationFirstNameLayout.error = null
            }

            when {
                lastName.isBlank() -> registrationLastNameLayout.error = getString(R.string.this_field_is_required)
                lastName.length < 3 -> registrationLastNameLayout.error = getString(R.string.must_be_at_least_3_characters)
                else -> registrationLastNameLayout.error = null
            }

            when {
                birthDate == 0L -> registrationBirthDateLayout.error = getString(R.string.this_field_is_required)
                birthDate > today.timeInMillis -> registrationBirthDateLayout.error = getString(R.string.birth_date_after_present)
                else -> registrationBirthDateLayout.error = null
            }

            when {
                login.isBlank() -> registrationLoginLayout.error = getString(R.string.this_field_is_required)
                login.length < 4 -> registrationLoginLayout.error = getString(R.string.must_be_at_least_4_characters)
                else -> registrationLoginLayout.error = null
            }

            if (password.isBlank()) registrationPasswordLayout.error = getString(R.string.this_field_is_required)
            else registrationLoginLayout.error = null

            when {
                passwordRepeat.isBlank() -> registrationPasswordRepeatLayout.error = getString(R.string.this_field_is_required)
                passwordRepeat != password -> registrationPasswordRepeatLayout.error = getString(R.string.passwords_didn_t_match)
                else -> registrationPasswordRepeatLayout.error = null
            }

            register(firstName, lastName, birthDate, sex, login, password, passwordRepeat)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        birthDateCalendar.set(Calendar.YEAR, year)
        birthDateCalendar.set(Calendar.MONTH, month)
        birthDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        registrationBirthDate.setText(dateFormat.format(Date(birthDateCalendar.timeInMillis)))
        when {
            birthDateCalendar.timeInMillis == 0L -> registrationBirthDateLayout.error = getString(R.string.this_field_is_required)
            birthDateCalendar.timeInMillis > today.timeInMillis -> registrationBirthDateLayout.error = getString(R.string.birth_date_after_present)
            else -> registrationBirthDateLayout.error = null
        }
    }

    private fun register(firstName: String, lastName: String, birthDate: Long,
                         sex: Boolean, login: String, password: String, passwordRepeat: String) {

        if (firstName.isNotBlank() && firstName.length >= 3 &&
            lastName.isNotBlank() && lastName.length >= 3 &&
            birthDate != 0L && birthDate <= today.timeInMillis
            && login.isNotBlank()  && login.length > 4 && password.isNotBlank() &&
            passwordRepeat.isNotBlank() && password == passwordRepeat) {
            registrationPresenter.register(firstName.trim(), lastName.trim(), birthDate, sex, login.trim(), password.trim())
        }
    }

    override fun showNoConnection() {
        Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show()
    }

    override fun showNetworkError() {
        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
    }

    override fun showServerError() {
        Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
    }

    override fun showRegisteredUsername() {
        Toast.makeText(this, "This username already registered", Toast.LENGTH_SHORT).show()
    }

    override fun showUnknownError() {
        Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
    }

    override fun openMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}