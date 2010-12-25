/*
 * This file is part of Hash It!.
 * 
 * Copyright (C) 2009-2011 Thilo-Alexander Ginkel.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.ginkel.hashit.Constants.FocusRequest;
import com.ginkel.hashit.util.HistoryManager;
import com.ginkel.hashit.util.NullAdapter;
import com.ginkel.hashit.util.SeedHelper;
import com.ginkel.hashit.util.cache.MemoryCacheService;
import com.ginkel.hashit.util.cache.MemoryCacheServiceImpl;

public class MainActivity extends Activity {
    private EditText siteTag;
    private AutoCompleteTextView autoCompleteSiteTag;
    private boolean ignoreTagChange;

    private EditText masterKey;
    private EditText hashWord;
    private Button hashPassword;

    private CharSequence originalHost;

    private boolean restoredState;
    private boolean restoredSiteTag;
    private boolean welcomeDisplayed;

    private FocusRequest focus = FocusRequest.NONE;

    /** A pattern used to extract a site tag from a host name */
    private static final Pattern SITE_PATTERN = Pattern
            .compile("^.*?([\\w\\d\\-]+)\\.((co|com|net|org|ac)\\.)?\\w+$");

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        siteTag = (EditText) findViewById(R.id.SiteTag);
        siteTag.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                synchronized (MainActivity.this) {
                    if (!ignoreTagChange) {
                        publishSiteTag(MainActivity.this, s.toString());
                    }
                }
            }
        });

        if (HashItApplication.SUPPORTS_HISTORY) {
            // Android 1.6 and higher
            autoCompleteSiteTag = (AutoCompleteTextView) siteTag;
        }

        masterKey = (EditText) findViewById(R.id.MasterKey);
        masterKey.setOnEditorActionListener(new OnEditorActionListener() {
            private long lastProcessedEvent;

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || actionId == EditorInfo.IME_ACTION_NEXT) {
                    /*
                     * the type and number of events depends on the soft keyboard in use, so
                     * debounce the event and make sure that only the first one triggers an action
                     */
                    if (System.currentTimeMillis() - lastProcessedEvent > 1000) {
                        hashPassword();
                        lastProcessedEvent = System.currentTimeMillis();
                    }
                    return true;
                }
                return false;
            }
        });

        hashWord = (EditText) findViewById(R.id.HashWord);
        hashWord.setEnabled(false);

        hashPassword = (Button) findViewById(R.id.Calculate);
        hashPassword.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                hashPassword();
            }
        });

        findViewById(R.id.Bump).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String tag = siteTag.getText().toString();
                try {
                    CharSequence newSiteTag = PasswordHasher.bumpSiteTag(tag);
                    siteTag.setText(newSiteTag);
                    publishSiteTag(v.getContext(), newSiteTag.toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getBaseContext(), R.string.Message_InvalidSiteTag,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        originalHost = null;

        focus = FocusRequest.SITE_TAG;

        Intent intent = getIntent();
        Log.d(Constants.LOG_TAG, "intent = " + intent);
        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            // we have been called via the "Send to" intent

            if (!restoredSiteTag) {
                /* we did not just switch back and forth between the password and the parameters tab */

                String uriCandidate = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d(Constants.LOG_TAG, "uriCandidate = " + uriCandidate);
                if (uriCandidate != null) {
                    try {
                        Uri uri = Uri.parse(uriCandidate);
                        String host = uri.getHost();
                        originalHost = host;

                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());
                        String site = prefs
                                .getString(String.format(Constants.SITE_MAP, host), null);

                        if (site != null) {
                            siteTag.setText(site);
                            publishSiteTag(this, site);
                            focus = FocusRequest.MASTER_KEY;
                        } else {
                            Log.d(Constants.LOG_TAG, "host = " + host);
                            Matcher siteExtractor = SITE_PATTERN.matcher(host);
                            if (siteExtractor.matches()) {
                                site = siteExtractor.group(1);
                                siteTag.setText(site);
                                publishSiteTag(this, site);
                                focus = FocusRequest.MASTER_KEY;
                            } else {
                                Toast.makeText(getBaseContext(), R.string.Message_SiteTagFailure,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(Constants.LOG_TAG, "Failed to retrieve intent URI", e);
                        Toast.makeText(getBaseContext(), R.string.Message_SiteTagFailure,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            findViewById(R.id.UsageInformation).setVisibility(View.GONE);
        } else {
            findViewById(R.id.UsageInformation).setVisibility(View.VISIBLE);
        }

        displayWelcomeScreen();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Constants.STATE_WELCOME_DISPLAYED, welcomeDisplayed);
        outState.putCharSequence(Constants.STATE_SITE_TAG, siteTag.getText());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        welcomeDisplayed = savedInstanceState.getBoolean(Constants.STATE_WELCOME_DISPLAYED,
                welcomeDisplayed);

        CharSequence savedSiteTag = savedInstanceState.getCharSequence(Constants.STATE_SITE_TAG);
        if (savedSiteTag != null) {
            siteTag.setText(savedSiteTag);
            restoredSiteTag = true;
        } else {
            restoredSiteTag = false;
        }

        restoredState = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!restoredState) {
            /* do not mess with input focus if we restored the state */

            /* focus changes do not seem to work in onCreate */
            switch (focus) {
                case SITE_TAG:
                    siteTag.requestFocus();
                    if (HashItApplication.SUPPORTS_HISTORY) {
                        autoCompleteSiteTag.dismissDropDown();
                    }
                    break;
                case MASTER_KEY:
                    masterKey.requestFocus();
                    break;
            }
        }

        if (HashItApplication.SUPPORTS_HISTORY) {
            // Site Tag history
            autoCompleteSiteTag.setThreshold(1);
            HistoryManager siteTagHistory;
            if ((siteTagHistory = HashItApplication.getApp(this).getHistoryManager()) == null) {
                HashItApplication.getApp(this).setHistoryManager(
                        siteTagHistory = new HistoryManager(this, Constants.SITE_TAGS,
                                R.layout.autocomplete_list));
            }
            if (getBool(Constants.ENABLE_HISTORY,
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()), null, true)) {
                autoCompleteSiteTag.setAdapter(siteTagHistory.getAdapter());
            } else {
                autoCompleteSiteTag.setAdapter(new NullAdapter<String>(this,
                        R.layout.autocomplete_list));
            }
        }

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        if (getStringAsInt(Constants.CACHE_DURATION, prefs, null, -1) > 0) {
            final Context ctx = getApplicationContext();
            ctx.bindService(new Intent(ctx, MemoryCacheServiceImpl.class), new ServiceConnection() {

                public void onServiceDisconnected(ComponentName name) {
                }

                public void onServiceConnected(ComponentName name, IBinder service) {
                    final String cachedKey = ((MemoryCacheService.Binder) service).getService()
                            .getEntry(Constants.MASTER_KEY_CACHE);

                    if (cachedKey != null) {
                        masterKey.setText(cachedKey);
                    }
                }
            }, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (HashItApplication.SUPPORTS_HISTORY) {
            // back up history
            HashItApplication.getApp(this).getHistoryManager().save();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.findItem(R.id.MenuItemAbout).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                View view = View.inflate(MainActivity.this, R.layout.about, null);
                TextView textView = (TextView) view.findViewById(R.id.message);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(R.string.Text_About);
                new AlertDialog.Builder(MainActivity.this).setTitle(R.string.Title_About)
                        .setView(view).setIcon(R.drawable.icon).show();
                return true;
            }
        });

        menu.findItem(R.id.MenuItemSettings).setOnMenuItemClickListener(
                new OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.setAction(Constants.ACTION_GLOBAL_PREFS);
                        MainActivity.this.startActivity(intent);

                        return true;
                    }
                });

        return true;
    }

    private static boolean getBool(String key, SharedPreferences prefs, SharedPreferences defaults,
            boolean def) {
        return prefs.getBoolean(key, defaults != null ? defaults.getBoolean(key, def) : def);
    }

    /**
     * Works around issue 2096.
     */
    private static int getStringAsInt(String key, SharedPreferences prefs,
            SharedPreferences defaults, int def) {
        return Integer.valueOf(prefs.getString(
                key,
                defaults != null ? defaults.getString(key, String.valueOf(def)) : String
                        .valueOf(def)));
    }

    /**
     * A convenience method to forward the current site tag to the application.
     */
    private static void publishSiteTag(Context ctx, String siteTag) {
        HashItApplication.getApp(ctx).setSiteTag(siteTag);
    }

    private void displayWelcomeScreen() {
        if (!welcomeDisplayed) {
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            Integer hideUpToVersion = prefs.getInt(Constants.HIDE_WELCOME_SCREEN, 0);
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                Log.e(Constants.LOG_TAG, "Could not retrieve package info", e);
            }
            final int versionCode = packageInfo == null ? Integer.MAX_VALUE
                    : packageInfo.versionCode;

            if (hideUpToVersion == null || packageInfo == null
                    || hideUpToVersion < packageInfo.versionCode) {
                View view = View.inflate(MainActivity.this, R.layout.welcome, null);
                TextView textView = (TextView) view.findViewById(R.id.message);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(R.string.Text_Welcome);
                final CheckBox dontShowAgain = (CheckBox) view
                        .findViewById(R.id.CheckBox_DoNotShowAgain);
                /*
                 * we can't reliably store the current version if we could not determine the package
                 * version code
                 */
                dontShowAgain.setEnabled(packageInfo != null);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.Title_Welcome)
                        .setView(view)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        if (dontShowAgain.isChecked()) {
                                            prefs.edit()
                                                    .putInt(Constants.HIDE_WELCOME_SCREEN,
                                                            versionCode).commit();
                                        }
                                    }
                                }).setIcon(R.drawable.icon).show();
                welcomeDisplayed = true;
            }
        }
    }

    private void hashPassword() {
        String tag = siteTag.getText().toString();
        final String key = masterKey.getText().toString();

        if (tag.length() == 0) {
            Toast.makeText(getBaseContext(), R.string.Message_SiteTagEmpty, Toast.LENGTH_LONG)
                    .show();
            siteTag.requestFocus();
        } else if (key.length() == 0) {
            Toast.makeText(getBaseContext(), R.string.Message_MasterKeyEmpty, Toast.LENGTH_LONG)
                    .show();
            masterKey.requestFocus();
        } else {
            SharedPreferences prefs = getSharedPreferences(tag, MODE_PRIVATE);
            SharedPreferences defaults = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            boolean compatibility = tag.startsWith(Constants.COMPATIBILITY_PREFIX);
            if (compatibility) {
                tag = tag.substring(Constants.COMPATIBILITY_PREFIX.length());
            } else {
                /*
                 * Assume compatibility mode to be the default iff some prefs have already been
                 * stored for a given tag
                 */
                compatibility = prefs.getBoolean(Constants.COMPATIBILITY_MODE, prefs.getAll()
                        .size() > 0)
                        || defaults.getBoolean(Constants.COMPATIBILITY_MODE, true);
            }

            Log.i(Constants.LOG_TAG,
                    String.format("Compatibility mode %s", compatibility ? "enabled" : "disabled"));

            if (!compatibility) {
                tag = PasswordHasher.hashPassword(SeedHelper.getSeed(defaults), tag, //
                        24, //
                        true, // require digits
                        true, // require punctuation
                        true, // require mixed case
                        false, // no special chars
                        false // only digits
                        );
            }

            String hash = PasswordHasher.hashPassword(tag, key, //
                    getStringAsInt(Constants.HASH_WORD_SIZE, prefs, defaults, 8), //
                    getBool(Constants.REQUIRE_DIGITS, prefs, defaults, true), //
                    getBool(Constants.REQUIRE_PUNCTUATION, prefs, defaults, true), //
                    getBool(Constants.REQUIRE_MIXED_CASE, prefs, defaults, true), //
                    getBool(Constants.RESTRICT_SPECIAL_CHARS, prefs, defaults, false), //
                    getBool(Constants.RESTRICT_DIGITS, prefs, defaults, false));

            hashWord.setText(hash, BufferType.NORMAL);

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(hash);

            if (originalHost != null) {
                // save site tag for host name
                defaults.edit().putString(String.format(Constants.SITE_MAP, originalHost), tag)
                        .commit();
            }

            if (HashItApplication.SUPPORTS_HISTORY
                    && defaults.getBoolean(Constants.ENABLE_HISTORY, true)) {
                HashItApplication.getApp(this).getHistoryManager().add(tag);
                masterKey.requestFocus();
            }

            final int cacheDuration = getStringAsInt(Constants.CACHE_DURATION, defaults, null, -1);
            if (cacheDuration > 0) {
                final Context ctx = getApplicationContext();
                MemoryCacheServiceImpl.ensureStarted(ctx);
                ctx.bindService(new Intent(ctx, MemoryCacheServiceImpl.class),
                        new ServiceConnection() {

                            public void onServiceDisconnected(ComponentName name) {
                            }

                            public void onServiceConnected(ComponentName name, IBinder service) {
                                ((MemoryCacheService.Binder) service).getService().putEntry(
                                        Constants.MASTER_KEY_CACHE, key, cacheDuration * 60 * 1000);
                            }
                        }, 0);
            }

            Toast.makeText(getBaseContext(), R.string.Message_HashCopiedToClipboard,
                    Toast.LENGTH_LONG).show();

            Intent intent = getIntent();
            if (defaults.getBoolean(Constants.AUTO_EXIT, false) && intent != null
                    && Intent.ACTION_SEND.equals(intent.getAction())) {
                finish();
            }
        }
    }
}
