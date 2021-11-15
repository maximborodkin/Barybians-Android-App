package ru.maxim.barybians.ui.activity.base

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import org.koin.android.ext.android.inject
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager

abstract class BaseActivity : MvpAppCompatActivity() {

    private val preferencesManager: PreferencesManager by inject()
    private var theme = preferencesManager.theme

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(when (preferencesManager.theme) {
            getString(R.string.theme_dark) -> R.style.DarkTheme
            else -> R.style.LightTheme
        })
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (theme != preferencesManager.theme){
            theme = preferencesManager.theme
            recreate()
        }
    }
}