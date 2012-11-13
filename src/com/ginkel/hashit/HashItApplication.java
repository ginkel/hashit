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

package com.ginkel.hashit;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.ginkel.hashit.util.HistoryManager;

/**
 * The Hash It! Application. Used to sync the site tag between different activities.
 *
 * @author Thilo-Alexander Ginkel
 */
public class HashItApplication extends Application {
    private String siteTag;
    private HistoryManager siteTagHistory;

    public static HashItApplication getApp(Context ctx) {
        return (HashItApplication) ctx.getApplicationContext();
    }

    protected String getSiteTag() {
        return siteTag;
    }

    protected void setSiteTag(String newSiteTag) {
        siteTag = newSiteTag;
    }

    protected HistoryManager getHistoryManager() {
        return siteTagHistory;
    }

    protected void setHistoryManager(HistoryManager historyManager) {
        siteTagHistory = historyManager;
    }

    protected PackageInfo getPackageInfo() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.LOG_TAG, "Could not retrieve package info", e);
        }
        return null;
    }

    protected int getVersion() {
        PackageInfo packageInfo = getPackageInfo();
        return packageInfo == null ? Integer.MAX_VALUE : packageInfo.versionCode;
    }
}
