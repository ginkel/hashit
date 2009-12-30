/*
 * This file is part of Password Genie.
 * 
 * Copyright (C) 2009 Thilo-Alexander Ginkel.
 * 
 * Password Genie is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Password Genie is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Password Genie.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ginkel.passwordhasher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;

public class SettingsActivity extends PreferenceActivity {

	/**
	 * Lists all preference keys that must be disabled if the "Digits only"
	 * restriction is in effect
	 */
	private static final String[] DEPS_RESTRICT_DIGITS_ONLY = {
			Constants.RESTRICT_SPECIAL_CHARS, Constants.REQUIRE_DIGITS,
			Constants.REQUIRE_MIXED_CASE, Constants.REQUIRE_PUNCTUATION };

	/**
	 * Lists all preference keys that must be disabled if the
	 * "Special characters" restriction is in effect
	 */
	private static final String[] DEPS_RESTRICT_SPECIAL_CHARS = { Constants.REQUIRE_PUNCTUATION };

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
		PreferenceManager prefManager = getPreferenceManager();

		Intent intent = getIntent();
		if (Constants.ACTION_SITE_PREFS.equals(intent.getAction())) {
			GenieApplication app = (GenieApplication) getApplication();
			prefManager.setSharedPreferencesName(app.getSiteTag());
		}

		/*
		 * an ugly hack to make sure the PreferenceActivity redraws based on the
		 * current preferences
		 */
		PreferenceScreen prefScreen = getPreferenceScreen();
		if (prefScreen != null) {
			prefScreen.removeAll();
		} else {
			prefScreen = prefManager.createPreferenceScreen(this);
			setPreferenceScreen(prefScreen);
		}

		SharedPreferences defaults = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// Requirements
		PreferenceCategory requirements = new PreferenceCategory(this);
		requirements.setTitle(R.string.Header_Requirements);
		prefScreen.addPreference(requirements);

		addCheckBoxPreference(requirements, Constants.REQUIRE_DIGITS,
				R.string.CheckBox_Digits, defaults, true);
		addCheckBoxPreference(requirements, Constants.REQUIRE_PUNCTUATION,
				R.string.CheckBox_Punctuation, defaults, true);
		addCheckBoxPreference(requirements, Constants.REQUIRE_MIXED_CASE,
				R.string.CheckBox_MixedCase, defaults, true);
		addListPreference(requirements, Constants.HASH_WORD_SIZE,
				R.string.Label_Size, R.string.Header_HashWordSize,
				R.array.Array_Sizes, R.array.Array_Sizes_Values, defaults, 8);

		// Restrictions
		PreferenceCategory restrictions = new PreferenceCategory(this);
		restrictions.setTitle(R.string.Header_Restrictions);
		prefScreen.addPreference(restrictions);

		addCheckBoxPreference(restrictions, Constants.RESTRICT_DIGITS,
				R.string.CheckBox_DigitsOnly, defaults, false)
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {

							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								disableConflictingPreferences(
										(Boolean) newValue, true,
										DEPS_RESTRICT_DIGITS_ONLY);
								return true;
							}
						});
		addCheckBoxPreference(restrictions, Constants.RESTRICT_SPECIAL_CHARS,
				R.string.CheckBox_NoSpecialChars, defaults, false)
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {

							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								disableConflictingPreferences(
										(Boolean) newValue, true,
										DEPS_RESTRICT_SPECIAL_CHARS);
								return true;
							}
						});

		// disable mutually exclusive preferences
		disableConflictingPreferences(Constants.RESTRICT_DIGITS,
				DEPS_RESTRICT_DIGITS_ONLY);
		disableConflictingPreferences(Constants.RESTRICT_SPECIAL_CHARS,
				DEPS_RESTRICT_SPECIAL_CHARS);

		super.onResume();
	}

	private ListPreference addListPreference(PreferenceCategory parent,
			String key, int labelResId, int dialogTitleResId, int entriesResId,
			int valuesResId, SharedPreferences defaults, int defaultValue) {
		ListPreference pref = new ListPreference(this);
		pref.setKey(key);
		pref.setTitle(labelResId);
		pref.setEntries(entriesResId);
		pref.setEntryValues(valuesResId);
		pref.setDialogTitle(dialogTitleResId);
		Object def = defaults.getAll().get(key);
		if (def == null) {
			def = String.valueOf(defaultValue);
		}
		pref.setDefaultValue(def);
		parent.addPreference(pref);
		return pref;
	}

	/**
	 * Adds a new {@link CheckBoxPreference} to a parent
	 * {@link PreferenceCategory} and returns the newly created preference
	 * (which may be further tweaked by the caller).
	 * 
	 * @param parent
	 *            the parent category
	 * @param key
	 *            the preference key
	 * @param resId
	 *            the string representing the pref's label to be displayed in
	 *            the prefs UI
	 * @param global
	 *            a global {@link SharedPreferences} object to derive the
	 *            preference's default value from
	 * @param defaultValue
	 *            the preference's default value (becoming effective of the
	 *            global {@link SharedPreferences} do not contain a default
	 *            value for this key)
	 * @return the newly created {@link CheckBoxPreference}
	 */
	private CheckBoxPreference addCheckBoxPreference(PreferenceCategory parent,
			String key, int resId, SharedPreferences global,
			boolean defaultValue) {
		CheckBoxPreference pref = new CheckBoxPreference(this);
		pref.setKey(key);
		pref.setTitle(resId);
		Object def = global.getAll().get(key);
		if (def == null) {
			def = defaultValue;
		}
		pref.setDefaultValue(def);
		parent.addPreference(pref);
		return pref;
	}

	/**
	 * Disables all preferences, which are in conflict with the preference
	 * identified by the specified key.
	 * 
	 * @param key
	 * @param dependencies
	 *            a set of dependencies to be disabled if the given preference
	 *            is set to "true"
	 */
	private void disableConflictingPreferences(String key,
			String... dependencies) {
		disableConflictingPreferences(getPreferenceManager()
				.findPreference(key).getSharedPreferences().getBoolean(key,
						false), false, dependencies);
	}

	/**
	 * Disables a set of preferences.
	 * 
	 * @param disable
	 *            indicates whether the preferences should be disabled
	 * @param dependencies
	 *            the preferences to be disabled
	 */
	private void disableConflictingPreferences(boolean disable, boolean reset,
			String... dependencies) {
		for (String dep : dependencies) {
			if (disable || reset) {
				getPreferenceManager().findPreference(dep).setEnabled(!disable);
			}
		}
	}
}
