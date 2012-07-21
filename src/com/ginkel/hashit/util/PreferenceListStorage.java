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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;

/**
 * An incarnation of the {@link ListSharedPreferences} interface adding the capability to persist
 * {@link String} {@link List}s over the standard Android implementation.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class PreferenceListStorage implements ListSharedPreferences {
    private static final String INTERNAL_LIST_PREFIX = "_List_";
    private static final String INTERNAL_LIST_SPECIAL_VALUE = "_PrefList_";

    private SharedPreferences prefs;
    private Map<OnSharedPreferenceChangeListener, ListAwareChangeListener> listeners;

    public PreferenceListStorage(SharedPreferences prefs) {
        this.prefs = prefs;
        this.listeners = new HashMap<OnSharedPreferenceChangeListener, ListAwareChangeListener>(0);
    }

    public boolean contains(String key) {
        return prefs.contains(key);
    }

    public ListEditor edit() {
        return new ListEditorImpl(prefs.edit());
    }

    public Map<String, ?> getAll() {
        // TODO not yet supported
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return prefs.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return prefs.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    /**
     * Retrieves a list of strings stored in the preferences bundle.
     * 
     * @param key
     *            the key under which the preferences list is stored
     * 
     * @return a (potentially empty) list of strings stored under the provided key
     * 
     * @throws IllegalArgumentException
     *             if the specified key references a non-list preference
     */
    public List<String> getStringList(String key) {
        String value = prefs.getString(key, null);

        if (value == null) {
            return new ArrayList<String>();
        } else if (!INTERNAL_LIST_SPECIAL_VALUE.equals(value)) {
            throw new IllegalArgumentException(String.format("%s is not a list value", key));
        }

        int count = prefs.getInt(INTERNAL_LIST_PREFIX + key + "_Count", 0);
        List<String> result = new ArrayList<String>(count);
        for (int ii = 0; ii < count; ii++) {
            value = prefs.getString(String.format("%s%s%d", INTERNAL_LIST_PREFIX, key, ii), null);
            result.add(value);
        }

        return result;
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (listeners) {
            ListAwareChangeListener listListener;
            if (!listeners.containsKey(listener)) {
                listeners.put(listener, listListener = new ListAwareChangeListener(listener));
                prefs.registerOnSharedPreferenceChangeListener(listListener);
            } else {
                throw new IllegalStateException("listener already registered");
            }
        }
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (listeners) {
            OnSharedPreferenceChangeListener listListener = listeners.remove(listener);
            if (listListener != null) {
                prefs.unregisterOnSharedPreferenceChangeListener(listListener);
            } else {
                throw new IllegalArgumentException("listener not registered");
            }
        }
    }

    private class ListAwareChangeListener implements OnSharedPreferenceChangeListener {
        private OnSharedPreferenceChangeListener listener;

        public ListAwareChangeListener(OnSharedPreferenceChangeListener listener) {
            this.listener = listener;
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!key.startsWith(INTERNAL_LIST_PREFIX)) {
                listener.onSharedPreferenceChanged(sharedPreferences, key);
            }
        }
    }

    private class ListEditorImpl implements ListEditor {
        private Editor editor;

        public ListEditorImpl(Editor editor) {
            this.editor = editor;
        }

        public Editor clear() {
            return editor.clear();
        }

        public boolean commit() {
            return editor.commit();
        }

        public ListEditor putBoolean(String key, boolean value) {
            editor.putBoolean(key, value);
            return this;
        }

        public ListEditor putFloat(String key, float value) {
            editor.putFloat(key, value);
            return this;
        }

        public ListEditor putInt(String key, int value) {
            editor.putInt(key, value);
            return this;
        }

        public ListEditor putLong(String key, long value) {
            editor.putLong(key, value);
            return this;
        }

        public ListEditor putString(String key, String value) {
            editor.putString(key, value);
            return this;
        }

        /**
         * Stores a collection of strings in this preferences bundle. Internally, the list is mapped
         * to multiple regular string keys, but caution is taken to avoid name collisions.
         * 
         * @param key
         *            the key to store the value under
         * @param value
         *            the collection of values to store under the specified key
         */
        public ListEditor putStrings(String key, Collection<String> value) {
            editor.putString(key, INTERNAL_LIST_SPECIAL_VALUE);
            editor.putInt(INTERNAL_LIST_PREFIX + key + "_Count", value.size());
            int ii = 0;
            for (String val : value) {
                editor.putString(String.format("%s%s%d", INTERNAL_LIST_PREFIX, key, ii++), val);
            }
            return this;
        }

        public Editor remove(String key) {
            return editor.remove(key);
        }

        public void apply() {
            // TODO Auto-generated method stub

        }

        public Editor putStringSet(String arg0, Set<String> arg1) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public Set<String> getStringSet(String arg0, Set<String> arg1) {
        // TODO Auto-generated method stub
        return null;
    }
}
