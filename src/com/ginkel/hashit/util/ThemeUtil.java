/*
 * This file is part of Hash It!.
 *
 * Copyright (C) 2009-2011 Thilo-Alexander Ginkel.
 * Copyright (C) 2011-2012 TG Byte Software GmbH.
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

package com.ginkel.hashit.util;

import android.app.Activity;
import android.preference.PreferenceManager;

import static android.R.style.Theme_Holo;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static com.ginkel.hashit.Constants.USE_DARK_THEME;

public final class ThemeUtil {
    private ThemeUtil() {
    }

    public static void applyTheme(Activity activity) {
        if (SDK_INT >= HONEYCOMB) {
            if (PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext()).getBoolean(USE_DARK_THEME, false))
                activity.setTheme(Theme_Holo);
        }
    }
}
