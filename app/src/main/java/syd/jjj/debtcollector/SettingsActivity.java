package syd.jjj.debtcollector;

import android.arch.persistence.room.Delete;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        DeleteAllConfirmationFragment.DeleteAllCallback {

    private SharedPreferences sharedPreferences;
    private String currentTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        currentTheme = sharedPreferences.getString("theme_settings_list", "defaultTheme");

        if (currentTheme.equals("defaultTheme")) {
            setTheme(R.style.DefaultAppTheme);
        }

        if (currentTheme.equals("wisteriaTheme")) {
            setTheme(R.style.Wisteria);
        }

        if(currentTheme.equals("softPastelTheme")){
            setTheme(R.style.SoftPastel);
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);



        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("theme_settings_list")) {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
    }

    @Override
    public void DeleteAllData(){

        new AsyncTask<DebtValue, Void, Void>() {
            @Override
            protected Void doInBackground(DebtValue... debtValues) {
                DebtValueDatabase debtValueDatabase =
                        DebtValueDatabaseAccessor.getInstance(getApplication());
                debtValueDatabase.debtValueDAO().deleteAllDebtValues();
                return null;
            }
        }.execute();
    }

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();  // optional depending on your needs
    }
}