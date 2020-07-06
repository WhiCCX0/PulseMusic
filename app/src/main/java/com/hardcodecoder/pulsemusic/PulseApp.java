package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class PulseApp extends Application {

    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        AppFileManager.initDataDir(this);
        ThemeManagerUtils.init(getApplicationContext());
        DimensionsUtil.init(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            final AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
            manager.initDynamicShortcuts(false);
            mListener = (sharedPreferences, key) -> {
                if (key.equals(Preferences.ACCENTS_COLOR_KEY)) manager.initDynamicShortcuts(true);
            };
            getSharedPreferences(Preferences.ACCENTS_COLOR_KEY, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(mListener);
        }
    }

    @Override
    public void onLowMemory() {
        getSharedPreferences(Preferences.ACCENTS_COLOR_KEY, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(mListener);
        super.onLowMemory();
    }
}
