package ru.maxim.barybians.ui.activity.preferences

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.service.Actions
import ru.maxim.barybians.service.ServiceState
import ru.maxim.barybians.utils.DialogFactory
import ru.maxim.barybians.utils.toast
import java.io.File

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_preferences)

        findPreference<SwitchPreferenceCompat>(PreferencesManager.isNotificationsEnabledKey)
            ?.setOnPreferenceChangeListener { preference, _ ->
                if ((preference as SwitchPreferenceCompat).isChecked) {
                    if (actionOnService(Actions.START)) return@setOnPreferenceChangeListener true
                } else {
                    if (actionOnService(Actions.STOP)) return@setOnPreferenceChangeListener true
                }
                false
            }

        findPreference<MultiSelectListPreference>(PreferencesManager.notificationSoundEffectKey)
            ?.setOnPreferenceChangeListener { preference, newValue ->

                true
            }

        findPreference<Preference>(PreferencesManager.clearNotificationsPoolKey)
            ?.setOnPreferenceClickListener {

                true
            }

        findPreference<Preference>(PreferencesManager.versionKey)?.summary =
            context?.packageName?.let { packageName ->
                context?.packageManager?.getPackageInfo(packageName, 0)?.versionName
            }

        setClearCacheSummary()

        findPreference<Preference>(PreferencesManager.clearCacheKey)?.setOnPreferenceClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                Glide.get(requireContext().applicationContext).clearDiskCache()
            }
            context?.toast(R.string.cache_cleared)
            setClearCacheSummary()
            true
        }

        findPreference<Preference>(PreferencesManager.logoutKey)?.setOnPreferenceClickListener {
            DialogFactory.createLogoutAlertDialog()
                .show(parentFragmentManager, "LogoutDialogFragment")
            true
        }

        findPreference<ListPreference>(PreferencesManager.themeKey)?.apply {
            setOnPreferenceChangeListener { _, _ ->
                activity?.recreate()
                true
            }
            setSummary(
                when (value) {
                    getString(R.string.theme_dark) -> R.string.dark
                    else -> R.string.light
                }
            )
        }
    }

    private fun setClearCacheSummary() {
        val cacheSize = getCacheSize()
        findPreference<Preference>(PreferencesManager.clearCacheKey)?.summary =
            when {
                cacheSize >= 1000_000 -> getString(
                    R.string.mbytes,
                    cacheSize.toInt() / 1000_000
                )
                cacheSize >= 1000 -> getString(R.string.kbytes, cacheSize.toInt() / 1000)
                else -> getString(R.string.bytes, cacheSize.toInt())
            }
    }

    private fun getCacheSize() =
        getDirSize(context?.cacheDir) + getDirSize(context?.externalCacheDir)

    private fun getDirSize(dir: File?): Long {
        if (dir == null) return 0
        var size = 0L
        for (file in dir.listFiles() ?: return 0) {
            if (file != null && file.isDirectory) {
                size += getDirSize(file)
            } else if (file != null && file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    private fun actionOnService(action: Actions): Boolean {
        if (PreferencesManager.serviceState == ServiceState.STOPPED.name && action == Actions.STOP) return false
        if (PreferencesManager.serviceState == ServiceState.STARTED.name && action == Actions.START) return false
//            Intent(context, MessageService::class.java).also {
//                it.action = action.name
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context?.startForegroundService(it)
//                } else{
//                    context?.startService(it)
//                }
//            }
        return true
    }
}