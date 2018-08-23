package ru.sukharev.focustimer.settings

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.*
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import ru.sukharev.focustimer.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragment() {

        private val ringtoneListener = Preference.OnPreferenceChangeListener{
            preference, value ->
            setRingtoneSummary(preference,value)
            true
        }

        private val timeListener = Preference.OnPreferenceChangeListener{
            preference, value ->
            setTimeSummary(preference,value)
            true
        }

        private fun setTimeSummary(preference: Preference?, value: Any?) {
            if (preference is DialogPreference) {
                val stringValue = value.toString()

                // Set the summary to reflect the new value.
                preference.setSummary(stringValue)
            }
        }

        private fun setRingtoneSummary(preference: Preference, value: Any?){
            val stringValue = value.toString()
            if (preference is RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(getString(R.string.silent))

                } else {
                    val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue))

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null)
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        val name = ringtone.getTitle(preference.getContext())
                        preference.setSummary(name)
                    }
                }
            }
        }

        override fun onCreate(savedInstanceState : Bundle?)
        {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            with (findPreference(getString(R.string.focus_ringtone_key))){
                onPreferenceChangeListener = ringtoneListener
                ringtoneListener.onPreferenceChange(this,
                        PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                                .getString(key,getString(R.string.focus_ringtone_default_key)))
            }
            with (findPreference(getString(R.string.focus_time_key))) {
                onPreferenceChangeListener = timeListener
                timeListener.onPreferenceChange(this,
                        PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
                        .getInt(key,resources.getInteger(R.integer.focus_time_default_value)))
            }


        }

    }
}
