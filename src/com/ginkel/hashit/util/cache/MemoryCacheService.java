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

package com.ginkel.hashit.util.cache;

/**
 * A memory cache service, which keeps its entries for a configurable amount of time.
 * 
 * @author Thilo-Alexander Ginkel
 */
public interface MemoryCacheService {
    interface Binder {
        /**
         * Retrieves an instance of this service interface.
         */
        MemoryCacheService getService();
    }

    /**
     * Retrieves an entry from the cache.
     * 
     * @param key
     *            the key identifying the entry
     * @return the requested entry (or <code>null</code> if the entry does not exist or has already
     *         expired)
     */
    String getEntry(final String key);

    /**
     * Puts an entry into the cache.
     * 
     * @param key
     *            the key identifying the entry
     * @param value
     *            the value to associate with the entry
     * @param retentionPeriod
     *            the retention period of the entry (in ms)
     */
    void putEntry(final String key, final String value, final long retentionPeriod);
}
