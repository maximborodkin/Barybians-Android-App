package ru.maxim.barybians.data.persistence

import android.content.SharedPreferences
import dagger.Reusable
import javax.inject.Inject

/**
 * Singleton object for access to SharedPreferences
 *  @property context uses applicationContext sets from [ru.maxim.barybians.App] class
 */
@Reusable
class PreferencesManager @Inject constructor(private val preferences: SharedPreferences) {
//
//    private val sharedPreferences: SharedPreferences by lazy {
//        PreferenceManager.getDefaultSharedPreferences(context)
//    }

    var isDarkMode: Boolean
        get() = preferences.getBoolean(isDarkModeKey, false)
        set(value) = preferences.edit().putBoolean(isDarkModeKey, value).apply()

    var token: String?
        get() = preferences.getString(tokenKey, null)
        set(value) = preferences.edit().putString(tokenKey, value).apply()

    var userId: Int
        get() = preferences.getInt(userIdKey, 0)
        set(value) = preferences.edit().putInt(userIdKey, value).apply()

    var userName: String
        get() = preferences.getString(userNameKey, "") ?: ""
        set(value) = preferences.edit().putString(userNameKey, value).apply()

    var userAvatar: String
        get() = preferences.getString(userAvatarKey, "") ?: ""
        set(value) = preferences.edit().putString(userAvatarKey, value).apply()

    companion object PreferencesKeys {
        const val isDarkModeKey = "dark_mode"
        private const val tokenKey = "token"
        private const val userIdKey = "user_id"
        private const val userNameKey = "user_name"
        private const val userAvatarKey = "user_avatar"
    }
}