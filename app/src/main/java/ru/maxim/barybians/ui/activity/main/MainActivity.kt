package ru.maxim.barybians.ui.activity.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ActivityMainBinding
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.utils.appComponent
import javax.inject.Inject

// Hihi hehe haha
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        if (preferencesManager.token.isNullOrBlank() || preferencesManager.userId == 0) {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            loginActivityIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginActivityIntent)
        } else {
            if (preferencesManager.isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            val binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            with(findNavController(R.id.mainFragmentHost)) {
                addOnDestinationChangedListener { _, _, arguments ->
                    binding.mainNavigationBottom.isVisible =
                        arguments != null && arguments.getBoolean(hasBottomNavigationKey, false)
                }
                binding.mainNavigationBottom.setupWithNavController(this)
            }
        }
    }

    companion object {
        const val hasBottomNavigationKey = "hasBottomNavigation"
    }
}