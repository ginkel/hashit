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

package com.ginkel.hashit.util;

import java.util.AbstractList;
import java.util.List;

import android.widget.ArrayAdapter;

/**
 * An adapter to convert an {@link ArrayAdapter} into a {@link List}.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class ArrayAdapterToListAdapter<E> extends AbstractList<E> {
    private final ArrayAdapter<E> adapter;

    public ArrayAdapterToListAdapter(ArrayAdapter<E> adapter) {
        this.adapter = adapter;
    }

    @Override
    public E get(int index) {
        return adapter.getItem(index);
    }

    @Override
    public int size() {
        return adapter.getCount();
    }
}
