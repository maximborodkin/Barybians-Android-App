package ru.maxim.barybians.ui.activity.profile

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_profile.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.BaseActivity
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()
        val userId = intent.getIntExtra("userId", PreferencesManager.userId)
        supportFragmentManager
            .beginTransaction()
            .replace(profileFragmentHost.id, ProfileFragment.newInstance(userId), "ProfileFragment")
            .commit()
    }
}