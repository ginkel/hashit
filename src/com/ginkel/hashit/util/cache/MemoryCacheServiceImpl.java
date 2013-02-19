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

package com.ginkel.hashit.util.cache;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.ginkel.hashit.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of the memory cache service declared in the {@link MemoryCacheService}
 * interface.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class MemoryCacheServiceImpl extends Service implements MemoryCacheService {
    private IBinder binder = new LocalBinder();
    private Map<String, CacheEntry<String>> cache = new HashMap<String, CacheEntry<String>>();
    private Handler handler;

    public class LocalBinder extends android.os.Binder implements MemoryCacheService.Binder {

        public MemoryCacheService getService() {
            return MemoryCacheServiceImpl.this;
        }
    }

    private static class CacheEntry<T> {
        private T value;
        private long expiresOn;

        public CacheEntry(T value, long expiresOn) {
            this.value = value;
            this.expiresOn = expiresOn;
        }

        public boolean hasExpired() {
            return expiresOn <= System.currentTimeMillis();
        }

        public T getValue() {
            return value;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * For pre-Eclair compatibility.
     */
    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent, startId);
        return START_STICKY;
    }

    private void handleCommand(Intent intent, int startId) {
        Log.i(Constants.LOG_TAG, String.format(
                "Start of service %s triggered by intent %s, startId %d", this.getClass()
                        .getCanonicalName(), intent, startId));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.handler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cache.clear();

        Log.i(Constants.LOG_TAG,
                String.format("Start service %s stopped", this.getClass().getCanonicalName()));
    }

    public String getEntry(final String key) {
        String result = null;
        synchronized (cache) {
            CacheEntry<String> entry = cache.get(key);
            if (entry != null) {
                if (entry.hasExpired()) {
                    cache.remove(key);
                } else {
                    result = entry.getValue();
                }
            }
        }
        return result;
    }

    public void putEntry(final String key, final String value, final long retentionPeriod) {
        synchronized (cache) {
            cache.put(key, new CacheEntry<String>(value, System.currentTimeMillis()
                    + retentionPeriod));
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                synchronized (cache) {
                    CacheEntry<String> entry = cache.get(key);
                    if (entry != null) {
                        if (entry.hasExpired()) {
                            Log.i(Constants.LOG_TAG, String.format(
                                    "Expiring %s entry from cache after %d ms", key,
                                    retentionPeriod));
                            cache.remove(key);
                        }
                    }
                }
            }
        }, retentionPeriod);
    }

    /**
     * Ensures that the service is running.
     */
    public static void ensureStarted(Context ctx) {
        ctx.startService(new Intent(ctx, MemoryCacheServiceImpl.class));
    }

    /**
     * Forcefully terminates the service.
     */
    public static void stopService(Context ctx) {
        ctx.stopService(new Intent(ctx, MemoryCacheServiceImpl.class));
    }
}
