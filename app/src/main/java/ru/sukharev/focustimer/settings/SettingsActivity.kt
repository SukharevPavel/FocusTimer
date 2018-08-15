package ru.sukharev.focustimer.settings

import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import ru.sukharev.focustimer.R

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(getFragmentManager().beginTransaction()) {
            replace(android.R.id.content, SettingsFragment())
            commit();
        }
    }

    class SettingsFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState : Bundle?)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
