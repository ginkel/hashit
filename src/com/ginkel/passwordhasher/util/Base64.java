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

package com.ginkel.passwordhasher.util;

import android.text.SpannableStringBuilder;

/**
 * A Base64 helper class.
 * 
 * @author Thilo-Alexander Ginkel
 */
public class Base64 {
	/** The padding character */
	private static final char PAD = '=';

	/**
	 * Converts a byte array into its base64 representation (skipping any
	 * trailing padding).
	 * 
	 * @param data
	 *            the data to convert
	 * @return the data's base64 representation as a string
	 */
	public static final String toBase64(byte[] data) {
		SpannableStringBuilder result = new SpannableStringBuilder(new String(
				org.apache.commons.codec.binary.Base64.encodeBase64(data)));
		// remove trailing padding
		while (result.length() > 0 && result.charAt(result.length() - 1) == PAD) {
			result.replace(result.length() - 1, result.length(), "");
		}
		return result.toString();
	}
}
