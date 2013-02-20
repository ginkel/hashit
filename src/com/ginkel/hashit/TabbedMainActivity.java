/*
 * This file is part of Hash It!.
 * 
 * Copyright (C) 2009-2011 Thilo-Alexander Ginkel.
 * Copyright (C) 2011-2013 TG Byte Software GmbH.
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
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import static com.ginkel.hashit.Constants.ACTION_GLOBAL_PREFS;
import static com.ginkel.hashit.Constants.USE_DARK_THEME;
import static com.ginkel.hashit.util.ThemeUtil.applyTheme;

public class TabbedMainActivity extends TabActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences settings;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        settings.registerOnSharedPreferenceChangeListener(this);

        applyTheme(this);

        /* Hide title bar for small screens */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            final Window win = getWindow();
            final int screenHeight = win.getWindowManager().getDefaultDisplay().getHeight();
            final int screenWidth = win.getWindowManager().getDefaultDisplay().getWidth();
            if ((screenHeight <= 240 && screenWidth <= 320)
                    || (screenHeight <= 320 && screenWidth <= 240)) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
        }

        /* Create tabs */
        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        /* Create home tab */
        TabSpec tab = tabHost.newTabSpec("home");
        Intent mainIntent = new Intent(this, PasswordActivity.class);
        mainIntent.fillIn(getIntent(), Intent.FILL_IN_DATA | Intent.FILL_IN_ACTION);
        tab.setContent(mainIntent);
        tab.setIndicator(resources.getText(R.string.Label_Password),
                resources.getDrawable(R.drawable.padlock_tab));
        tabHost.addTab(tab);

        /* Create parameters tab */
        tab = tabHost.newTabSpec("settings");
        Intent hiddenAgenda = new Intent(this, ParametersActivity.class);
        hiddenAgenda.setAction(Constants.ACTION_SITE_PREFS);
        tab.setContent(hiddenAgenda);
        tab.setIndicator(resources.getText(R.string.Label_Settings),
                resources.getDrawable(R.drawable.wrench_tab));
        tabHost.addTab(tab);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (settings != null)
            settings.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.findItem(R.id.MenuItemAbout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                View view = View.inflate(TabbedMainActivity.this, R.layout.about, null);
                TextView textView = (TextView) view.findViewById(R.id.message);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(R.string.Text_About);
                new AlertDialog.Builder(TabbedMainActivity.this).setTitle(R.string.Title_About)
                        .setView(view).setIcon(R.drawable.icon).show();
                return true;
            }
        });

        menu.findItem(R.id.MenuItemSettings).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(TabbedMainActivity.this, SettingsActivity.class);
                        intent.setAction(ACTION_GLOBAL_PREFS);
                        TabbedMainActivity.this.startActivity(intent);

                        return true;
                    }
                });

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (USE_DARK_THEME.equals(key))
            recreate();
    }
}
