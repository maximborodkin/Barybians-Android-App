package ru.maxim.barybians.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.ActivityMainBinding
import ru.maxim.barybians.service.WebSocketService
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
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Open login screen if the token or userId is invalid
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentHost) as NavHostFragment
        val graph = navHostFragment.navController.navInflater.inflate(R.navigation.main_nav_graph)
        if (preferencesManager.token.isNullOrBlank() || preferencesManager.userId <= 0) {
            graph.setStartDestination(R.id.loginFragment)
        } else {
            graph.setStartDestination(R.id.postsListFragment)
            val messageServiceIntent = Intent(this, WebSocketService::class.java)
            startService(messageServiceIntent)
        }
        navHostFragment.navController.graph = graph

        // Hide BottomNavigationView for each fragment except Feed, Chats and Profile
        with(navHostFragment.navController) {
            addOnDestinationChangedListener { _, _, arguments ->
                binding.mainNavigationBottom.isVisible = arguments != null && arguments.getBoolean("isRoot", false)
            }
            binding.mainNavigationBottom.setupWithNavController(this)
        }
    }
}