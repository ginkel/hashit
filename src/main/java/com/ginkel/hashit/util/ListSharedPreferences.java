/*
 * This file is part of Hash It!.
 * 
 * Copyright (C) 2009-2011 Thilo-Alexander Ginkel.
 * Copyright (C) 2011-2014 TG Byte Software GmbH.
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

import java.util.Collection;
import java.util.List;

import android.content.SharedPreferences;

/**
 * A {@link SharedPreferences} extension, which supports storing {@link List}s of {@link String}s.
 * 
 * @author Thilo-Alexander Ginkel
 */
public interface ListSharedPreferences extends SharedPreferences {
    public ListEditor edit();

    List<String> getStringList(String key);

    public interface ListEditor extends SharedPreferences.Editor {
        ListEditor putBoolean(String key, boolean value);

        ListEditor putFloat(String key, float value);

        ListEditor putInt(String key, int value);

        ListEditor putLong(String key, long value);

        ListEditor putString(String key, String value);

        ListEditor putStrings(String key, Collection<String> value);
    }
}
