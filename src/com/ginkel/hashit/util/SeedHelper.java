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

import java.util.Locale;
import java.util.UUID;

import android.content.SharedPreferences;

import com.ginkel.hashit.Constants;

/**
 * A helper class to support to support storing and retrieving the seed information required for
 * Password Hasher Plus compatibility.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class SeedHelper {

    /**
     * Retrieves the current seed from the provided {@link SharedPreferences} or generates a new one
     * (and stores it in the prefs).
     */
    public static String getSeed(SharedPreferences prefs) {
        // yep, this is not thread-safe, but it does not need to be
        String result = prefs.getString(Constants.SEED, null);
        if (result == null) {
            storeSeed(result = UUID.randomUUID().toString().toUpperCase(Locale.ENGLISH), prefs);
        }
        return result;
    }

    /**
     * Stores the seed in the provided {@link SharedPreferences}.
     */
    public static void storeSeed(String seed, SharedPreferences prefs) {
        prefs.edit().putString(Constants.SEED, seed).commit();
    }
}
