/*
 * This file is part of Hash It!.
 * 
 * Copyright (C) 2009-2010 Thilo-Alexander Ginkel.
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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.ginkel.hashit.util.HistoryManager;

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
        PreferenceScreen prefScreen = getPreferenceScreen();

        SharedPreferences defaults = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        // History
        PreferenceCategory convenience = new PreferenceCategory(this);
        convenience.setTitle(R.string.Header_Convenience);
        prefScreen.addPreference(convenience);
        Preference enableHistory = addCheckBoxPreference(convenience, Constants.ENABLE_HISTORY,
                R.string.CheckBox_EnableHistory, defaults, HashItApplication.SUPPORTS_HISTORY);
        if (HashItApplication.SUPPORTS_HISTORY) {
            enableHistory.setSummary(R.string.Summary_EnableHistory);
        } else {
            enableHistory.setSummary(R.string.Summary_EnableHistory_Cupcake);
            enableHistory.setEnabled(false);
        }
        boolean enableClear = historyManager != null && !historyManager.isEmpty();
        Preference clear = addActionPreference(convenience, R.string.Action_ClearHistory,
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
        Preference autoExit = addCheckBoxPreference(convenience, Constants.AUTO_EXIT,
                R.string.CheckBox_AutoExit, defaults, false);
        autoExit.setSummary(R.string.Summary_AutoExit);
    }
}
