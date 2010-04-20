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

package com.ginkel.hashit.util;

import java.lang.reflect.Field;

import android.os.Build;

public class VersionHelper {
    private static int SDK_INT = -1;

    /**
     * Determines the Android SDK level.
     */
    public static int getSdkVersion() {
        if (SDK_INT < 0) {
            synchronized (VersionHelper.class) {
                if (SDK_INT < 0) {
                    try {
                        Field sdkLevel = Build.VERSION.class.getField("SDK_INT");
                        SDK_INT = sdkLevel.getInt(null);
                    } catch (Exception e) {
                        // fall back to string version
                        SDK_INT = Integer.parseInt(Build.VERSION.SDK);
                    }
                }
            }
        }

        return SDK_INT;
    }
}
