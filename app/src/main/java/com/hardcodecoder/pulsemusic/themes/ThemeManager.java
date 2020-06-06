package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils.DayTime;

class ThemeManager {

    private boolean mAutoMode;
    private boolean mDarkMode;
    private int mThemeId;

    ThemeManager(Context context) {
        mAutoMode = AppSettings.isAutoThemeEnabled(context);
        if (mAutoMode) mDarkMode = (DayTimeUtils.getTimeOfDay() == DayTime.NIGHT);
        else mDarkMode = AppSettings.isDarkModeEnabled(context);

        if (mDarkMode) mThemeId = AppSettings.getSelectedDarkTheme(context);
        else mThemeId = ThemeStore.LIGHT_THEME;
    }

    boolean isAutoModeEnabled() {
        return mAutoMode;
    }

    boolean isDarkModeEnabled() {
        return mDarkMode;
    }

    @StyleRes
    int getThemeToApply() {
        return ThemeStore.getThemeById(mDarkMode, mThemeId);
    }
}
