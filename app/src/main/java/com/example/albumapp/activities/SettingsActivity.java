package com.example.albumapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.albumapp.R;
import com.example.albumapp.fragments.SettingsFragment;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar_settings;
    public static final String KEY_PREF_LANGUAGE = "changeLanguage";
    public boolean languagePref_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        toolbarEvents();

        getFragmentManager().beginTransaction()
                .replace(R.id.settingsFragment, new SettingsFragment())
                .commit();


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        if (key.equals(KEY_PREF_LANGUAGE)) {
                            languagePref_ID = prefs.getBoolean(SettingsActivity.KEY_PREF_LANGUAGE, false);
                            if(languagePref_ID){
                                Locale localeVI = new Locale("vi-rVN");
                                setLocale(localeVI);
                            }
                            else{
                                Locale localeEN = new Locale("en");
                                setLocale(localeEN);
                            }
                        }
                    }
                };
        sharedPref.registerOnSharedPreferenceChangeListener(listener);

    }


    private void toolbarEvents(){
        toolbar_settings = (Toolbar)findViewById(R.id.toolbar_settings);
        toolbar_settings.setNavigationIcon(R.drawable.ic_back);
        toolbar_settings.setTitle(getBaseContext().getResources().getString(R.string.settings));
        toolbar_settings.inflateMenu(R.menu.menu_top_settings);
        toolbar_settings.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_LANGUAGE)) {
            languagePref_ID = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LANGUAGE,false);
            if(languagePref_ID){
                Locale localeVI = new Locale("vi-rVN");
                setLocale(localeVI);
            }
            else{
                Locale localeEN = new Locale("en");
                setLocale(localeEN);
            }
        }
    }

    private Locale nameToLocale(String language) {
        Log.d("Simple-Gallery","SettingsActivity: nameToLocal");
        switch(language) {
            case "English":
                return new Locale("en");
            case "Polski":
                return new Locale("pl");
            case "Tiếng Việt":
                return new Locale("vi-rVN");
            default:
                return new Locale("en");
        }
    }
    public void setLocale(Locale locale) {
        Log.d("Simple-Gallery","SettingsActivity: setLocale");
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        recreate();
    }

}