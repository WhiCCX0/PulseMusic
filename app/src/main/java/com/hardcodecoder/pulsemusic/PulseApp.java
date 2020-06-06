package com.hardcodecoder.pulsemusic;

import android.app.Application;

import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class PulseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManagerUtils.init(getApplicationContext());
        DimensionsUtil.init(this);
    }
}
