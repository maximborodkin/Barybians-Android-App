package ru.maxim.barybians.ui.activity.profile

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_profile.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.base.BaseActivity
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()
        val userId = intent.getIntExtra("userId", PreferencesManager.userId)
        val profileFragment = ProfileFragment().apply { arguments = intent.extras }
        supportFragmentManager
            .beginTransaction()
            .replace(profileFragmentHost.id, profileFragment, "ProfileFragment#${userId}")
            .commit()
    }
}