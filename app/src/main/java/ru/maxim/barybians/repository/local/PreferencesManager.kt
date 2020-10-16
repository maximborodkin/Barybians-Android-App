package ru.maxim.barybians.repository.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager.context

/**
 * Singleton object for access to SharedPreferences
 *  @property context uses applicationContext sets from [ru.maxim.barybians.App] class
 */
object PreferencesManager {
    
    lateinit var context: Context
    private val sharedPreferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    /**
     * SharedPreferences keys
     */
    val enableNotificationsServiceKey by lazy { context.getString(R.string.enable_notifications_service_preference) }
    val notificationsSoundEffectsKey by lazy { context.getString(R.string.notification_sound_effects_preference) }
    val clearNotificationsPoolKey by lazy { context.getString(R.string.clear_notifications_pool_preference) }
    val themeKey by lazy { context.getString(R.string.theme_preference) }
    val versionKey by lazy { context.getString(R.string.build_version_preference) }
    val clearCacheKey by lazy { context.getString(R.string.clear_cache_preference) }
    val logoutKey by lazy { context.getString(R.string.logout_preference) }
    val tokenKey by lazy { context.getString(R.string.token_preference) }
    val userIdKey by lazy { context.getString(R.string.user_id_preference) }
    val userNameKey by lazy { context.getString(R.string.user_name_preference) }
    val userAvatarKey by lazy { context.getString(R.string.user_avatar_preference) }

    /**
     * Preference access methods
     */
    val isNotificationsEnabled: Boolean = sharedPreferences.getBoolean(enableNotificationsServiceKey, true)

    val notificationsSoundEffects: Set<String> = sharedPreferences.getStringSet(
            notificationsSoundEffectsKey,
            context.resources.getStringArray(R.array.notification_effects_default_values).toSet()
    )!!

    val theme: String = sharedPreferences.getString(themeKey, context.getString(R.string.theme_light))!!

    var token: String?
        get() = sharedPreferences.getString(tokenKey, null)
        set(value) { sharedPreferences.edit().putString(tokenKey, value).apply() }

    var userId: Int
        get() = sharedPreferences.getInt(userIdKey, 0)
        set(value) { sharedPreferences.edit().putInt(userIdKey, value).apply() }

    var userName: String
        get() = sharedPreferences.getString(userNameKey, "")!!
        set(value) { sharedPreferences.edit().putString(userNameKey, value).apply() }

    var userAvatar: String
        get() = sharedPreferences.getString(userAvatarKey, "")!!
        set(value) { sharedPreferences.edit().putString(userAvatarKey, value).apply() }
}