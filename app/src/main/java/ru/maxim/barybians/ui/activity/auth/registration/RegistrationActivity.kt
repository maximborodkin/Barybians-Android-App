package ru.maxim.barybians.ui.activity.auth.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import ru.maxim.barybians.R

class RegistrationActivity : MvpAppCompatActivity(), RegistrationView, DatePickerDialog.OnDateSetListener {

    @InjectPresenter
    lateinit var registrationPresenter: RegistrationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()
    }

    override fun showNoConnection() {}

    override fun showNetworkError() {}

    override fun showServerError() {}

    override fun showRegisteredUsername() {}

    override fun showUnknownError() {}

    override fun openMainActivity() {}

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {}
}