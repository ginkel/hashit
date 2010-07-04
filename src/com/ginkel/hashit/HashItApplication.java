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

package com.ginkel.hashit;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.ginkel.hashit.util.HistoryManager;
import com.ginkel.hashit.util.VersionHelper;

/**
 * The Hash It! Application. Used to sync the site tag between different activities.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class HashItApplication extends Application {
    private String siteTag;
    private HistoryManager siteTagHistory;

    public static final boolean SUPPORTS_HISTORY = VersionHelper.getSdkVersion() >= Build.VERSION_CODES.DONUT;

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
}
