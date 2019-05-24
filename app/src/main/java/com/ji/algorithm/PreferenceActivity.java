package com.ji.algorithm;

import android.os.Bundle;
import android.preference.Preference;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        Preference preference = new Preference(this, null);
        preference.setTitle("setTitle setTitle setTitle setTitle setTitle setTitle setTitle setTitle");
        preference.setSummary("setSummary setSummary setSummary setSummary setSummary setSummary setSummary setSummary");
        preference.setSelectable(false);
        getPreferenceScreen().addPreference(preference);
    }
}
