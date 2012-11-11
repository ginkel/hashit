/*
 * This file is part of Hash It!.
 * 
 * Copyright (C) 2009-2011 Thilo-Alexander Ginkel.
 * 
 * Hash It! is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Hash It! is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hash It!.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ginkel.hashit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.widget.EditText;
import com.ginkel.hashit.util.HistoryManager;
import com.ginkel.hashit.util.SeedHelper;
import com.ginkel.hashit.util.cache.MemoryCacheServiceImpl;

/**
 * An activity for the global application preferences (including default hash parameters, which are
 * inherited from {@link ParametersActivity}).
 *
 * @author Thilo-Alexander Ginkel
 */
public class SettingsActivity extends ParametersActivity {

    /**
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        final HistoryManager historyManager = HashItApplication.getApp(this).getHistoryManager();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        final SharedPreferences defaults = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        // Convenience
        final PreferenceCategory convenience = new PreferenceCategory(this);
        convenience.setTitle(R.string.Header_Convenience);
        prefScreen.addPreference(convenience);

        final Preference enableHistory = addCheckBoxPreference(convenience,
                Constants.ENABLE_HISTORY, R.string.CheckBox_EnableHistory, defaults,
                true);
        enableHistory.setSummary(R.string.Summary_EnableHistory);

        final boolean enableClear = historyManager != null && !historyManager.isEmpty();
        final Preference clear = addActionPreference(convenience, R.string.Action_ClearHistory,
                new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference pref) {
                        historyManager.clear();
                        pref.setSummary(R.string.Summary_ClearHistory_Empty);
                        pref.setEnabled(false);
                        return true;
                    }
                }, enableClear);
        clear.setSummary(enableClear ? R.string.Summary_ClearHistory
                : R.string.Summary_ClearHistory_Empty);

        final Preference autoExit = addCheckBoxPreference(convenience, Constants.AUTO_EXIT,
                R.string.CheckBox_AutoExit, defaults, false);
        autoExit.setSummary(R.string.Summary_AutoExit);

        final ListPreference cacheDuration = addListPreference(convenience,
                Constants.CACHE_DURATION, R.string.Label_CacheMasterKey,
                R.string.Header_CacheDuration, R.array.Array_CacheDuration,
                R.array.Array_CacheDuration_Values, defaults, -1);
        cacheDuration.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((ListPreference) preference, newValue);
                if (Integer.parseInt((String) newValue) <= 0) {
                    MemoryCacheServiceImpl.stopService(SettingsActivity.this);
                }
                return true;
            }
        });
        updateSummary(cacheDuration, cacheDuration.getValue());
    }

    @Override
    protected void populateSecurityCategory(final PreferenceCategory security,
                                            final SharedPreferences defaults) {
        final Preference compatibilityMode = addCheckBoxPreference(security,
                Constants.COMPATIBILITY_MODE, R.string.CheckBox_CompatibilityMode, defaults, true);
        compatibilityMode.setSummary(R.string.Summary_CompatibilityMode);
        compatibilityMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(final Preference pref, final Object newValue) {
                if (Boolean.FALSE == newValue) {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle(R.string.Title_SeedWarning)
                            .setMessage(R.string.Message_SeedWarning)
                            .setPositiveButton(android.R.string.ok, new DummyOnClickListener())
                            .setIcon(R.drawable.icon).show();
                }

                return true;
            }
        });
        final Preference setPrivateKey = addActionPreference(security, R.string.Action_ChangeSeed,
                new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference pref) {
                        final View view = View.inflate(SettingsActivity.this, R.layout.seed_entry,
                                null);

                        final EditText seed = (EditText) view.findViewById(R.id.Edit_SeedEntry);
                        seed.setText(SeedHelper.getSeed(defaults));

                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle(R.string.Title_ChangeSeed)
                                .setView(view)
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                SeedHelper.storeSeed(seed.getText().toString(),
                                                        defaults);
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel,
                                        new DummyOnClickListener()).setCancelable(true)
                                .setIcon(R.drawable.icon).show();
                        return true;
                    }
                }, true);
        setPrivateKey.setSummary(R.string.Summary_ChangeSeed);
    }

    private static class DummyOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
        }
    }
}
