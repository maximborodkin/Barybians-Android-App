package ru.maxim.barybians.ui.activity.main

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import ru.maxim.barybians.R
import ru.maxim.barybians.ui.activity.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = Navigation.findNavController(this, R.id.mainFragmentHost)
        NavigationUI.setupWithNavController(mainNavigationBottom, navController)
    }
}