package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils.DayTime;

public class ThemeManager {

    private static boolean mAutoMode;
    private static boolean mDarkMode;
    private static int mThemeId;

    public static void init(Context context) {
        mAutoMode = AppSettings.isAutoThemeEnabled(context);
        if (mAutoMode) mDarkMode = (DayTimeUtils.getTimeOfDay() == DayTime.NIGHT);
        else mDarkMode = AppSettings.isDarkModeEnabled(context);

        if (mDarkMode) mThemeId = AppSettings.getSelectedDarkTheme(context);
        else mThemeId = ThemeStore.LIGHT_THEME;
    }

    static boolean isAutoModeEnabled() {
        return mAutoMode;
    }

    public static boolean isDarkModeEnabled() {
        return mDarkMode;
    }

    static void enableDarkMode(Context context, boolean enable) {
        AppSettings.enableDarkMode(context, enable);
    }

    static void enableAutoTheme(Context context, boolean enable) {
        mAutoMode = enable;
        AppSettings.enableAutoTheme(context, enable);
    }

    public static void setSelectedDarkTheme(Context context, int id) {
        mThemeId = id;
        AppSettings.saveSelectedDarkTheme(context, mThemeId);
    }

    @StyleRes
    public static int getThemeToApply() {
        return ThemeStore.getThemeById(mDarkMode, mThemeId);
    }
}
