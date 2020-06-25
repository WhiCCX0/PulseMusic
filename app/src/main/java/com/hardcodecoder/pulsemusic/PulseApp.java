package com.hardcodecoder.pulsemusic;

import android.app.Application;
import android.os.Build;

import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class PulseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppFileManager.initDataDir(this);
        ThemeManagerUtils.init(getApplicationContext());
        DimensionsUtil.init(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
            manager.initDynamicShortcuts();
        }
    }
}
