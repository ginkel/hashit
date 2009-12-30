/*
 * This file is part of Password Genie.
 * 
 * Copyright (C) 2009 Thilo-Alexander Ginkel.
 * 
 * Password Genie is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Password Genie is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Password Genie.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ginkel.passwordhasher;

import android.app.Application;

/**
 * The Password Genie Application. Used to sync the site tag between different
 * activities.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class GenieApplication extends Application {
	private String siteTag;

	protected String getSiteTag() {
		return siteTag;
	}

	protected void setSiteTag(String newSiteTag) {
		siteTag = newSiteTag;
	}
}
