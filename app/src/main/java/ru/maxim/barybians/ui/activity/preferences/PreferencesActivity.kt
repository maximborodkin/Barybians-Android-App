package ru.maxim.barybians.ui.activity.preferences

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.library.BuildConfig
import androidx.fragment.app.DialogFragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.maxim.barybians.R
import ru.maxim.barybians.repository.local.PreferencesManager
import ru.maxim.barybians.ui.activity.auth.login.LoginActivity
import ru.maxim.barybians.ui.activity.base.BaseActivity
import java.io.File


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

    class PreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.fragment_preferences)

            findPreference<Preference>(PreferencesManager.versionKey)?.summary = BuildConfig.VERSION_NAME

            val cacheSize = getCacheSize()
            findPreference<Preference>(PreferencesManager.clearCacheKey)?.summary =
                when{
                    cacheSize >= 1000_000 -> getString(R.string.mbytes, cacheSize.toInt()/1000_000)
                    cacheSize >= 1000 -> getString(R.string.kbytes, cacheSize.toInt()/1000)
                    else -> getString(R.string.bytes, cacheSize.toInt())
                }

            findPreference<Preference>(PreferencesManager.clearCacheKey)?.setOnPreferenceClickListener {
                // TODO("Clear Glide and database cache")
//                lifecycleScope.launch(Dispatchers.IO) {
//                    val glide = Glide.get(requireContext().applicationContext)
//                    glide.clearMemory()
//                    glide.clearDiskCache()
//                }
                Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
                true
            }

            findPreference<Preference>(PreferencesManager.logoutKey)?.setOnPreferenceClickListener {
                LogoutAlertDialog().show(parentFragmentManager, "LogoutDialogFragment")
                true
            }

            findPreference<ListPreference>(PreferencesManager.themeKey)?.apply {
                setOnPreferenceChangeListener { _, _ ->
                    activity?.recreate()
                    true
                }
                setSummary(when(this.value) {
                    getString(R.string.theme_dark) -> R.string.dark
                    else -> R.string.light
                })
            }
        }

        private fun getCacheSize() =
            getDirSize(context?.cacheDir) +
            getDirSize(context?.externalCacheDir)


        private fun getDirSize(dir: File?): Long {
            if (dir == null) return 0
            var size = 0L
            for (file in dir.listFiles()?:return 0) {
                if (file != null && file.isDirectory) {
                    size += getDirSize(file)
                } else if (file != null && file.isFile) {
                    size += file.length()
                }
            }
            return size
        }
    }

    class LogoutAlertDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?) =
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(getString(R.string.are_you_sure))
                setPositiveButton(R.string.yes) { _, _ ->
                    PreferencesManager.token = null
                    PreferencesManager.userId = 0
                    val loginActivityIntent = Intent(context, LoginActivity::class.java)
                    loginActivityIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginActivityIntent)
                }
                setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            }.create()
    }
}
