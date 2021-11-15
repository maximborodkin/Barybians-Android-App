package ru.maxim.barybians.data.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.maxim.barybians.R
import ru.maxim.barybians.service.ServiceState

/**
 * Singleton object for access to SharedPreferences
 *  @property context uses applicationContext sets from [ru.maxim.barybians.App] class
 */
class PreferencesManager(val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * SharedPreferences keys
     */
    val isNotificationsEnabledKey by lazy { context.getString(R.string.enable_notifications_service_preference) }
    val notificationSoundEffectKey by lazy { context.getString(R.string.notification_sound_effect_preference) }
    val clearNotificationsPoolKey by lazy { context.getString(R.string.clear_notifications_pool_preference) }
    val versionKey by lazy { context.getString(R.string.build_version_preference) }
    val themeKey by lazy { context.getString(R.string.theme_preference) }
    val tokenKey by lazy { context.getString(R.string.token_preference) }
    val clearCacheKey by lazy { context.getString(R.string.clear_cache_preference) }
    val userIdKey by lazy { context.getString(R.string.user_id_preference) }
    val userNameKey by lazy { context.getString(R.string.user_name_preference) }
    val userAvatarKey by lazy { context.getString(R.string.user_avatar_preference) }
    val logoutKey by lazy { context.getString(R.string.logout_preference) }
    val serviceStateKey by lazy { context.getString(R.string.service_state_preference) }

    /**
     * Preference access methods
     */
    var isNotificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean(isNotificationsEnabledKey, true)
        set(value) { sharedPreferences.edit().putBoolean(isNotificationsEnabledKey, value).apply() }

    var theme: String
        get() = sharedPreferences.getString(themeKey, context.getString(R.string.theme_light))!!
        set(value) { sharedPreferences.edit().putString(themeKey, value).apply() }

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

    var serviceState: String
        get() = sharedPreferences.getString(serviceStateKey, ServiceState.STOPPED.name)!!
        set(value) { sharedPreferences.edit().putString(serviceStateKey, value).apply() }
}