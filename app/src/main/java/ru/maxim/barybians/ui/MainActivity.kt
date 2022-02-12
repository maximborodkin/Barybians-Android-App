package ru.maxim.barybians.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.maxim.barybians.R
import ru.maxim.barybians.data.persistence.PreferencesManager
import ru.maxim.barybians.databinding.ActivityMainBinding
import ru.maxim.barybians.utils.appComponent
import javax.inject.Inject

// Hihi hehe haha
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        // Setup the dark mode
        if (preferencesManager.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackground)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Open login screen if the token or userId is invalid
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentHost) as NavHostFragment
        val graph = navHostFragment.navController.navInflater.inflate(R.navigation.main_nav_graph)
        val startDestination =
            if (preferencesManager.token.isNullOrBlank() || preferencesManager.userId <= 0) R.id.loginFragment
            else R.id.feedFragment
        graph.setStartDestination(startDestination)
        navHostFragment.navController.graph = graph

        // Hide BottomNavigationView on each fragment except the first three
        with(navHostFragment.navController) {
            addOnDestinationChangedListener { _, _, arguments ->
                binding.mainNavigationBottom.isVisible =
                    arguments != null && arguments.getBoolean(hasBottomNavigationKey, false)
            }
            binding.mainNavigationBottom.setupWithNavController(this)
        }
    }

    companion object {
        const val hasBottomNavigationKey = "hasBottomNavigation"
    }
}