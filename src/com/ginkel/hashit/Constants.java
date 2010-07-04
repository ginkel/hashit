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

public interface Constants {
    final String HASH_WORD_SIZE = "HashWordSize";

    final String RESTRICT_SPECIAL_CHARS = "RestrictSpecialChars";
    final String RESTRICT_DIGITS = "RestrictDigits";

    final String REQUIRE_MIXED_CASE = "RequireMixedCase";
    final String REQUIRE_DIGITS = "RequireDigits";
    final String REQUIRE_PUNCTUATION = "RequirePunctuation";

    final String SITE_MAP = "Site:%s";

    final String HIDE_WELCOME_SCREEN = "HideWelcomeScreen";

    final String STATE_SITE_TAG = "SiteTag";
    final String STATE_WELCOME_DISPLAYED = "WelcomeScreenDisplayed";

    final String SITE_TAGS = "SiteTags";
    final String ENABLE_HISTORY = "EnableHistory";

    final String ACTION_GLOBAL_PREFS = "com.ginkel.hashit.GLOBAL_PREFS";
    final String ACTION_SITE_PREFS = "com.ginkel.hashit.SITE_PREFS";

    final String LOG_TAG = "HashIt";

    enum FocusRequest {
        NONE, SITE_TAG, MASTER_KEY
    }
}
