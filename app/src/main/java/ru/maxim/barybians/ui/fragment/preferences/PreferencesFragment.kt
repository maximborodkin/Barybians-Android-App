package ru.maxim.barybians.ui.fragment.preferences

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.repository.auth.AuthRepository
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.toast
import java.io.File
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.fragment_preferences)
        setClearCacheSummary()

        findPreference<SwitchPreference>(PreferencesManager.isDarkModeKey)?.setOnPreferenceChangeListener { _, _ ->
            activity?.recreate()
            true
        }

        findPreference<Preference>(buildVersion)?.summary =
            context?.packageName?.let { packageName ->
                context?.packageManager?.getPackageInfo(packageName, 0)?.versionName
            }

        findPreference<Preference>(clearCache)?.setOnPreferenceClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                Glide.get(requireContext().applicationContext).clearDiskCache()
            }
            context?.toast(R.string.cache_cleared)
            setClearCacheSummary()
            true
        }

        findPreference<Preference>(logout)?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(context ?: return@setOnPreferenceClickListener false).apply {
                setTitle(getString(R.string.are_you_sure))
                setPositiveButton(R.string.yes) { _, _ ->
                    authRepository.logout()
                    activity?.recreate()
                }
                setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            }.create()
            return@setOnPreferenceClickListener true
        }
    }

    private fun setClearCacheSummary() {
        val cacheSize = getDirSize(context?.cacheDir) + getDirSize(context?.externalCacheDir)
        findPreference<Preference>(clearCache)?.summary = when {
            cacheSize >= 1_000_000 -> getString(R.string.mbytes, cacheSize.toInt() / 1_000_000)
            cacheSize >= 1_000 -> getString(R.string.kbytes, cacheSize.toInt() / 1_000)
            else -> getString(R.string.bytes, cacheSize.toInt())
        }
    }

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

    companion object PreferencesMenuKeys {
        private const val darkMode = "dark_mode"
        private const val debugMode = "debug_mode"
        private const val clearCache = "clear_cache"
        private const val logout = "logout"
        private const val buildVersion = "build_version"
    }
}