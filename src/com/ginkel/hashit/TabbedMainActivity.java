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

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabbedMainActivity extends TabActivity {

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Hide title bar for small screens */
        final Window win = getWindow();
        final int screenHeight = win.getWindowManager().getDefaultDisplay().getHeight();
        final int screenWidth = win.getWindowManager().getDefaultDisplay().getWidth();
        if ((screenHeight <= 240 && screenWidth <= 320)
                || (screenHeight <= 320 && screenWidth <= 240)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        /* Create tabs */
        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        /* Create home tab */
        TabSpec tab = tabHost.newTabSpec("home");
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.fillIn(getIntent(), Intent.FILL_IN_DATA | Intent.FILL_IN_ACTION);
        tab.setContent(mainIntent);
        tab.setIndicator(resources.getText(R.string.Label_Password), resources
                .getDrawable(R.drawable.padlock_tab));
        tabHost.addTab(tab);

        /* Create settings tab */
        tab = tabHost.newTabSpec("settings");
        Intent hiddenAgenda = new Intent(this, SettingsActivity.class);
        hiddenAgenda.setAction(Constants.ACTION_SITE_PREFS);
        tab.setContent(hiddenAgenda);
        tab.setIndicator(resources.getText(R.string.Label_Settings), resources
                .getDrawable(R.drawable.wrench_tab));
        tabHost.addTab(tab);
    }
}
