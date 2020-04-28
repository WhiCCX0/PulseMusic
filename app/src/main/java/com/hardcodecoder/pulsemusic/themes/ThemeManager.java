package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils.DayTime;

public class ThemeManager {

    private static boolean mDarkMode = false;
    private static int mThemeId;
    private static int mAccentId;

    public static void init(Context context) {
        boolean auto = AppSettings.isAutoThemeEnabled(context);
        if (auto) mDarkMode = (DayTimeUtils.getTimeOfDay() == DayTime.NIGHT);
        else mDarkMode = AppSettings.isDarkModeEnabled(context);

        if (mDarkMode) mThemeId = AppSettings.getSelectedDarkTheme(context);
        else mThemeId = ThemeStore.LIGHT_THEME_1;
        mAccentId = AppSettings.getSelectedAccentColor(context);
    }

    public static boolean isDarkModeEnabled() {
        return mDarkMode;
    }

    public static void enableDarkMode(Context context, boolean enable) {
        AppSettings.enableDarkMode(context, enable);
    }

    public static void enableAutoTheme(Context context, boolean enable) {
        AppSettings.enableAutoTheme(context, enable);
    }

    public static void setSelectedDarkTheme(Context context, int id) {
        mThemeId = id;
        AppSettings.saveSelectedDarkTheme(context, mThemeId);
    }

    public static void setSelectedAccentColor(Context context, int id) {
        mAccentId = id;
        AppSettings.saveSelectedAccentColor(context, id);
    }

    @StyleRes
    public static int getThemeToApply() {
        return ThemeStore.getThemeById(mDarkMode, mThemeId);
    }

    @StyleRes
    public static int getAccentToApply() {
        return ThemeStore.getAccentById(mDarkMode, mAccentId);
    }
}
