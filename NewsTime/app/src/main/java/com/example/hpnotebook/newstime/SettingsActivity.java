package com.example.hpnotebook.newstime;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Hp Notebook on 29-01-2018.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getName();
    private static Preference mPreference;
    private static String dob;
    public static String PREFS_NAME = "date_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener{

        private static final String KEY_PREF_DATE = "date";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference section = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryToValue(section);
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
            Preference pgSize = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(pgSize);
            Preference date = findPreference(getString(R.string.settings_date_key));
            date.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDatePicker();
                    return true;
                }
            });
            mPreference = date;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            if(preference.getKey().equals(KEY_PREF_DATE)){
                Log.v(LOG_TAG,"inide if");
                SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.settings_date_key), dob);
                Log.v(LOG_TAG, dob + " inside bindPreferenceSummaryToValue");
                editor.apply();
            }
            SharedPreferences preferences = preference.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            // PreferenceManager.setDefaultValues(getActivity() , R.xml.settings_main, false);
            String preferenceString = preferences.getString(preference.getKey(), "");
            preference.setSummary(preferenceString);
            // onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(KEY_PREF_DATE)){

                // Set summary to be the user-description for the selected value
                Preference date = findPreference(key);
                String dateValue = sharedPreferences.getString(key, "");
                Log.v(LOG_TAG, "inside onSharedPreferenceChanged " + dateValue);
                date.setSummary(dateValue);
            }
        }

        @Override
        public void onResume() {
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        private void showDatePicker() {

            Log.v(LOG_TAG, "inside showDatePicker");
            DatePickerFragment date = new DatePickerFragment();

            /**
             * Set Up Current Date Into dialog
             */
            Calendar calender = Calendar.getInstance();
            Bundle args = new Bundle();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
            date.setArguments(args);

            /**
             * Set Call back to capture selected date
             */
            date.setCallBack(ondate);
            date.show(getFragmentManager(), "Date Picker");
        }

        DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dob = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth);
                Log.v(LOG_TAG, dob + " inside showDatePicker");
                bindPreferenceSummaryToValue(mPreference);
            }
        };
    }
}
