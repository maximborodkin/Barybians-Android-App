package ru.maxim.barybians.ui.activity.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import org.koin.android.ext.android.inject
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.ActivityMainBinding
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferencesManager.token.isNullOrEmpty() || preferencesManager.userId == 0) {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            loginActivityIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginActivityIntent)
        } else {
            val binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            with(findNavController(R.id.mainFragmentHost)) {
                addOnDestinationChangedListener { _, _, arguments ->
                    binding.mainNavigationBottom.isVisible =
                        arguments != null && arguments.getBoolean("hasBottomNavigation", false)
                }
                binding.mainNavigationBottom.setupWithNavController(this)
            }
        }
    }
}