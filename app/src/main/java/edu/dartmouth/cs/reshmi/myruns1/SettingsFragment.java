package edu.dartmouth.cs.reshmi.myruns1;


import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Settings fragment that displays user settings.
 *
 * @author Reshmi Suresh
 */
public class SettingsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // load preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
