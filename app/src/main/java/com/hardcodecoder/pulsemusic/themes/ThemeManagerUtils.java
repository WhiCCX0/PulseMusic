package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;

import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils;

public class ThemeManagerUtils {

    private static boolean mAutoMode = false;
    private static boolean mDarkMode = false;
    private static int mThemeId = ThemeStore.LIGHT_THEME;

    public static void init(Context context) {
        mAutoMode = AppSettings.isAutoThemeEnabled(context);
        if (mAutoMode) mDarkMode = (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
        else mDarkMode = AppSettings.isDarkModeEnabled(context);

        if (mDarkMode) mThemeId = AppSettings.getSelectedDarkTheme(context);
        else mThemeId = ThemeStore.LIGHT_THEME;
    }

    public static boolean toggleDarkTheme(Context context, boolean enabled) {
        AppSettings.enableDarkMode(context, enabled);
        //return true if activity needs to restart because theme needs to be changed
        return (!mDarkMode && enabled) || (mDarkMode && !enabled);
    }

    public static boolean toggleAutoTheme(Context context, boolean enabled) {
        AppSettings.enableAutoTheme(context, enabled);
        // User want auto theme based on time of day
        // If its night/day and current theme is light/dark respectively
        // them restart to apply new theme
        return (enabled && needToChangeTheme());

    }

    public static void setSelectedDarkTheme(Context context, int id) {
        AppSettings.saveSelectedDarkTheme(context, id);
    }

    public static boolean needToApplyNewDarkTheme() {
        //Returns true to restart activity in order to apply new dark theme
        return (mAutoMode && isNight()) || mDarkMode;
    }

    public static boolean isDarkModeEnabled() {
        return mDarkMode;
    }

    public static int getThemeToApply() {
        return ThemeStore.getThemeById(mDarkMode, mThemeId);
    }

    private static boolean needToChangeTheme() {
        return ((isNight() && !mDarkMode) || (!isNight() && mDarkMode));
    }

    private static boolean isNight() {
        return (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
    }
}
