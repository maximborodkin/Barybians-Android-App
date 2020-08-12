package ru.maxim.barybians.ui.base

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager


abstract class BaseActivity : MvpAppCompatActivity() {

    private var theme = PreferencesManager.theme

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(when (PreferencesManager.theme) {
            getString(R.string.theme_dark) -> R.style.DarkTheme
            else -> R.style.LightTheme
        })
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (theme != PreferencesManager.theme){
            theme = PreferencesManager.theme
            recreate()
        }
    }
}