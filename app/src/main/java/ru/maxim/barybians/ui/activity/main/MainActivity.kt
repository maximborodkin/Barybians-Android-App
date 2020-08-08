package ru.maxim.barybians.ui.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.BaseActivity
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = Navigation.findNavController(this, R.id.mainFragmentHost)
        NavigationUI.setupWithNavController(mainNavigationBottom, navController)
        val token = PreferencesManager.token
        val id = PreferencesManager.userId
        if (token.isNullOrEmpty() || id == 0){
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
            startActivity(loginActivityIntent)
        }
    }
}