package ru.maxim.barybians.ui.activity.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatFragment
import kotlinx.android.synthetic.main.activity_main.*
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.base.BaseActivity
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.ui.fragment.dialogsList.DialogsListFragment
import ru.maxim.barybians.ui.fragment.feed.FeedFragment
import ru.maxim.barybians.ui.fragment.profile.ProfileFragment

class MainActivity : BaseActivity() {

    private lateinit var feedFragment: FeedFragment
    private lateinit var dialogsLisFragment: DialogsListFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = PreferencesManager.token
        val id = PreferencesManager.userId

        if (token.isNullOrEmpty() || id == 0) {
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            loginActivityIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginActivityIntent)
        } else {
            val userIdBundle = Bundle().apply { putInt("userId", id) }

            if (savedInstanceState == null){
                feedFragment = FeedFragment().apply { arguments = userIdBundle; retainInstance = true }
                dialogsLisFragment = DialogsListFragment().apply { arguments = userIdBundle; retainInstance = true }
                profileFragment = ProfileFragment().apply { arguments = userIdBundle; retainInstance = true }

                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.mainFragmentHost, feedFragment, null).hide(feedFragment)
                    .add(R.id.mainFragmentHost, dialogsLisFragment, null).hide(dialogsLisFragment)
                    .add(R.id.mainFragmentHost, profileFragment, null).hide(profileFragment)
                    .commit()

                mainNavigationBottom.selectedItemId = R.id.profileFragment
                setFragment(R.id.profileFragment)
            } else {
                val bundleFeedFragment = supportFragmentManager.getFragment(savedInstanceState, "FeedFragment")
                feedFragment = if ((bundleFeedFragment as? FeedFragment) != null) bundleFeedFragment
                else FeedFragment().apply { arguments = userIdBundle; retainInstance = true }

                val bundleDialogsListFragment = supportFragmentManager.getFragment(savedInstanceState, "DialogsListFragment")
                dialogsLisFragment = if ((bundleDialogsListFragment as? DialogsListFragment) != null) bundleDialogsListFragment
                else DialogsListFragment().apply { arguments = userIdBundle; retainInstance = true }

                val bundleProfileFragment = supportFragmentManager.getFragment(savedInstanceState, "ProfileFragment")
                profileFragment = if ((bundleProfileFragment as? ProfileFragment) != null) bundleProfileFragment
                else ProfileFragment().apply { arguments = userIdBundle; retainInstance = true }
            }

            mainNavigationBottom.setOnNavigationItemSelectedListener { menuItem ->
                return@setOnNavigationItemSelectedListener setFragment(menuItem.itemId)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedItemId", mainNavigationBottom.selectedItemId)
        supportFragmentManager.putFragment(outState, "FeedFragment", feedFragment)
        supportFragmentManager.putFragment(outState, "DialogsListFragment", dialogsLisFragment)
        supportFragmentManager.putFragment(outState, "ProfileFragment", profileFragment)
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