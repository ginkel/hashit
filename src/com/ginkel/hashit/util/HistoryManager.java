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

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Manages the Site Tag history.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class HistoryManager {
    private final ListSharedPreferences prefs;
    private final String prefsKey;
    private final ArrayAdapter<String> adapter;
    private final ArrayAdapterToListAdapter<String> listAdapter;

    private boolean empty;

    public HistoryManager(Context context, String prefsKey, int textViewResourceId) {
        prefs = new PreferenceListStorage(context.getSharedPreferences("history",
                Context.MODE_PRIVATE));
        this.prefsKey = prefsKey;
        List<String> list;
        adapter = new ArrayAdapter<String>(context, textViewResourceId,
                list = prefs.getStringList(prefsKey));
        listAdapter = new ArrayAdapterToListAdapter<String>(adapter);
        empty = list.isEmpty();
    }

    public void add(String historyItem) {
        // make sure that we do not end up inserting duplicates
        adapter.remove(historyItem);
        adapter.insert(historyItem, 0);
        empty = false;
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public List<String> getHistory() {
        return listAdapter;
    }

    public boolean isEmpty() {
        /*
         * Unfortunately ArrayAdapter.getCount() does not reliably report the right size of the
         * array, so we must track this on our own
         */
        return empty;
    }

    public void clear() {
        adapter.clear();
        prefs.edit().clear().commit();
        empty = true;
    }

    public void save() {
        prefs.edit().putStrings(prefsKey, getHistory()).commit();
    }
}
