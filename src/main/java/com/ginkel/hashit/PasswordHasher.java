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

package com.ginkel.hashit;

import android.util.Log;
import com.ginkel.hashit.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordHasher {
    /** The MAC to be used for hashing the site tag */
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /** A pattern that matches numerical digits */
    private static final Pattern NUM_PATTERN = Pattern.compile("[^0-9]");

    /** Matches all special characters */
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[^a-z0-9]",
            Pattern.CASE_INSENSITIVE);

    /** Matches a site tag */
    private static final Pattern SITE_TAG_PATTERN = Pattern.compile("^(.*):([0-9]+)?$");

    /**
     * The password hashing function. Takes the site tag and master key as input and returns the
     * hash word (dependent on additional parameters that specify the domain of the hash word).
     */
    public static String hashPassword(String siteTag, String masterKey, int hashWordSize,
            boolean requireDigit, boolean requirePunctuation, boolean requireMixedCase,
            boolean restrictSpecial, boolean restrictDigits) {
        try {
            Mac hmacSha1 = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(masterKey.getBytes(), HMAC_SHA1_ALGORITHM);
            hmacSha1.init(key);
            String hash = Base64.toBase64(hmacSha1.doFinal(siteTag.getBytes()));

            int sum = 0;
            for (int ii = 0; ii < hash.length(); ii++)
                sum += hash.charAt(ii);

            // Restrict digits just does a mod 10 of all the characters
            if (restrictDigits)
                hash = convertToDigits(hash, sum, hashWordSize);
            else {
                // Inject digit, punctuation, and mixed case as needed.
                if (requireDigit)
                    hash = injectSpecialCharacter(hash, 0, 4, sum, hashWordSize, '0', 10);
                if (requirePunctuation && !restrictSpecial)
                    hash = injectSpecialCharacter(hash, 1, 4, sum, hashWordSize, '!', 15);
                if (requireMixedCase) {
                    hash = injectSpecialCharacter(hash, 2, 4, sum, hashWordSize, 'A', 26);
                    hash = injectSpecialCharacter(hash, 3, 4, sum, hashWordSize, 'a', 26);
                }
                // Strip out special characters as needed.
                if (restrictSpecial)
                    hash = removeSpecialCharacters(hash, sum, hashWordSize);
            }

            // Trim it to size.
            return hash.substring(0, hashWordSize);
        } catch (NoSuchAlgorithmException e) {
            Log.e(Constants.LOG_TAG, HMAC_SHA1_ALGORITHM + " not present", e);
            throw new IllegalStateException(HMAC_SHA1_ALGORITHM + " not present");
        } catch (InvalidKeyException e) {
            Log.e(Constants.LOG_TAG, "Failed to hash secret", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Inject a character chosen from a range of character codes into a block at the front of a
     * string if one of those characters is not already present.
     */
    private static String injectSpecialCharacter(String sInput, int offset, int reserved, int seed,
            int lenOut, char cStart, int cNum) {
        int pos0 = seed % lenOut;
        int pos = (pos0 + offset) % lenOut;
        // Check if a qualified character is already present
        // Write the loop so that the reserved block is ignored.
        for (int i = 0; i < lenOut - reserved; i++) {
            int i2 = (pos0 + reserved + i) % lenOut;
            char c = sInput.charAt(i2);
            if (c >= cStart && c < cStart + cNum)
                return sInput; // Already present - nothing to do
        }

        StringBuilder result = new StringBuilder();
        if (pos > 0) {
            result.append(sInput.substring(0, pos));
        }
        result.append((char) (((seed + sInput.charAt(pos)) % cNum) + cStart));
        if (pos + 1 < sInput.length()) {
            result.append(sInput.substring(pos + 1, sInput.length()));
        }
        return result.toString();
    }

    /**
     * Replace special characters by digits and numbers.
     */
    private static String removeSpecialCharacters(String sInput, int seed, int lenOut) {
        StringBuilder s = new StringBuilder(lenOut);
        int ii = 0;
        while (ii < lenOut) {
            Matcher m = SPECIAL_PATTERN.matcher(sInput.substring(ii));
            if (!m.find())
                break;
            int matchPos = m.start();
            if (matchPos > 0)
                s.append(sInput.substring(ii, ii + matchPos));
            s.append((char) ((seed + ii) % 26 + 65));
            ii += (matchPos + 1);
        }
        if (ii < sInput.length())
            s.append(sInput.substring(ii));
        return s.toString();
    }

    /**
     * Converts the input string to a digits-only representation.
     */
    private static String convertToDigits(String sInput, int seed, int lenOut) {
        StringBuilder s = new StringBuilder(lenOut);
        int ii = 0;
        while (ii < lenOut) {
            Matcher m = NUM_PATTERN.matcher(sInput.substring(ii));
            if (!m.find())
                break;
            int matchPos = m.start();
            if (matchPos > 0) {
                s.append(sInput.substring(ii, ii + matchPos));
            }
            s.append((char) ((seed + sInput.charAt(ii)) % 10 + 48));
            ii += (matchPos + 1);
        }
        if (ii < sInput.length()) {
            s.append(sInput.substring(ii));
        }
        return s.toString();
    }

    /**
     * Bumps the site tag.
     * 
     * @param siteTag
     *            the original site tag that should be bumped
     * @return the bumped site tag
     * @throws NumberFormatException
     *             if the current site tag is not correctly formatted
     */
    public static String bumpSiteTag(CharSequence siteTag) {
        Matcher m = SITE_TAG_PATTERN.matcher(siteTag);
        if (m.matches()) {
            return m.replaceFirst(String.format("$1:%d", Integer.parseInt(m.group(2)) + 1));
        } else if (siteTag.length() > 0) {
            return String.format("%s:1", siteTag);
        }
        return siteTag.toString();
    }
}