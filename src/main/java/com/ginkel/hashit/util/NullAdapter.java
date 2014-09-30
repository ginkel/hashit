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

import android.content.Context;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

/**
 * An {@link Adapter}, which never returns any data.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class NullAdapter<T> extends ArrayAdapter<T> {

    public NullAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
}
