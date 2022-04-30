package ru.maxim.barybians.data

import android.content.SharedPreferences
import dagger.Reusable
import javax.inject.Inject

/**
 * Singleton util class for access to the SharedPreferences
 */
@Reusable
class PreferencesManager @Inject constructor(private val preferences: SharedPreferences) {

    var isDarkMode: Boolean
        get() = preferences.getBoolean(isDarkModeKey, false)
        set(value) = preferences.edit().putBoolean(isDarkModeKey, value).apply()

    var isDebug: Boolean
        get() = preferences.getBoolean(isDebugKey, false)
        set(value) = preferences.edit().putBoolean(isDebugKey, value).apply()

    var token: String?
        get() = preferences.getString(tokenKey, null)
        set(value) = preferences.edit().putString(tokenKey, value).apply()

    var userId: Int
        get() = preferences.getInt(userIdKey, 0)
        set(value) = preferences.edit().putInt(userIdKey, value).apply()

    companion object PreferencesKeys {
        const val isDarkModeKey = "dark_mode"
        const val isDebugKey = "debug"
        private const val tokenKey = "token"
        private const val userIdKey = "user_id"
    }
}