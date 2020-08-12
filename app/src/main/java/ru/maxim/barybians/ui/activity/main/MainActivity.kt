package ru.maxim.barybians.ui.activity.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatFragment
import kotlinx.android.synthetic.main.activity_main.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.BaseActivity
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.ui.fragment.dialogsList.DialogsListFragment
import ru.maxim.barybians.ui.fragment.feed.FeedFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment
import java.lang.IllegalStateException


class MainActivity : BaseActivity() {

    private lateinit var feedFragment: FeedFragment
    private lateinit var dialogsLisFragment: DialogsListFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val token = PreferencesManager.token
        val id = PreferencesManager.userId
        if (token.isNullOrEmpty() || id == 0){
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            loginActivityIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginActivityIntent)
        } else {
            val userIdBundle = Bundle().apply { putInt("userId", id) }
            feedFragment = FeedFragment().apply { arguments = userIdBundle }
            dialogsLisFragment = DialogsListFragment().apply { arguments = userIdBundle }
            profileFragment = ProfileFragment().apply { arguments = userIdBundle }

            supportFragmentManager
                .beginTransaction()
                .add(R.id.mainFragmentHost, feedFragment, "FeedFragmentMain").hide(feedFragment)
                .add(R.id.mainFragmentHost, dialogsLisFragment, "DialogsListFragmentMain").hide(dialogsLisFragment)
                .add(R.id.mainFragmentHost, profileFragment, "profileFragmentMain").hide(profileFragment)
                .commit()

            if (savedInstanceState == null) {
                mainNavigationBottom.selectedItemId = R.id.profileFragment
                setFragment(R.id.profileFragment)
            }

            mainNavigationBottom.setOnNavigationItemSelectedListener { menuItem ->
                return@setOnNavigationItemSelectedListener setFragment(menuItem.itemId)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedItemId", mainNavigationBottom.selectedItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.profileFragment)
        mainNavigationBottom.selectedItemId = selectedItemId
        setFragment(mainNavigationBottom.selectedItemId)
    }

    private fun setFragment(fragmentId: Int): Boolean {
        val currentFragment: MvpAppCompatFragment = when(mainNavigationBottom.selectedItemId) {
            R.id.feedFragment -> feedFragment
            R.id.dialogsListFragment -> dialogsLisFragment
            R.id.profileFragment -> profileFragment
            else -> throw IllegalStateException("Unknown fragment id")
        }
        return when (fragmentId) {
            R.id.feedFragment -> {
                supportFragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(feedFragment)
                    .commit()
                true
            }
            R.id.dialogsListFragment -> {
                supportFragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(dialogsLisFragment)
                    .commit()
                true
            }
            R.id.profileFragment -> {
                supportFragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(profileFragment)
                    .commit()
                true
            }
            else -> false
        }
    }
}