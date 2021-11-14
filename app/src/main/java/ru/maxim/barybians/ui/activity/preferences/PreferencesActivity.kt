package ru.maxim.barybians.ui.activity.preferences

import android.os.Bundle
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.activity.base.BaseActivity

class PreferencesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.preferences)
        if (savedInstanceState == null)
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, PreferencesFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

