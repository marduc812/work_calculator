package com.marduc812.workcalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.util.Calendar;
import java.util.logging.Handler;

/**
 * Created by marduc on 08/09/16.
 */



public class SettingsActivity extends com.fnp.materialpreferences.PreferenceActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceFragment(new MyPreferenceFragment());

        View view = getListView();
        Snackbar.make(view,"Some features in order to apply, you need to restart the app",Snackbar.LENGTH_LONG).show();

    }

    public static class MyPreferenceFragment extends com.fnp.materialpreferences.PreferenceFragment {
        @Override
        public int addPreferencesFromResource()
        {
            return R.xml.preferences;
        }


    }


}