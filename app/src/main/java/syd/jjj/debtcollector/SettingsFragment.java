package syd.jjj.debtcollector;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        Preference deleteAllPreference = findPreference("theme_delete");
        deleteAllPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DeleteAllConfirmationFragment deleteAllConfirmationFragment = new DeleteAllConfirmationFragment();
                deleteAllConfirmationFragment.show(fm, "delete_all_confirmation_fragment");
                return true;
            }
        });
    }



}
