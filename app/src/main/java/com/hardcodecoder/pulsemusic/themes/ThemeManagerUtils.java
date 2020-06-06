package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;

import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.DayTimeUtils;

public class ThemeManagerUtils {

    private static ThemeManager mThemeManager;

    public static void init(Context context) {
        mThemeManager = new ThemeManager(context);
    }

    public static boolean toggleDarkTheme(Context context, boolean enabled) {
        //ThemeManager.enableDarkMode(context, enabled);
        AppSettings.enableDarkMode(context, enabled);
        //return true if activity needs to restart because theme needs to be changed
        return (!mThemeManager.isDarkModeEnabled() && enabled) || (mThemeManager.isDarkModeEnabled() && !enabled);
    }

    public static boolean toggleAutoTheme(Context context, boolean enabled) {
        //ThemeManager.enableAutoTheme(context, enabled);
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
        return (mThemeManager.isAutoModeEnabled() && isNight()) || mThemeManager.isDarkModeEnabled();
    }

    public static boolean isDarkModeEnabled() {
        return mThemeManager.isDarkModeEnabled();
    }

    public static int getThemeToApply() {
        return mThemeManager.getThemeToApply();
    }

    private static boolean needToChangeTheme() {
        return ((isNight() && !mThemeManager.isDarkModeEnabled()) || (!isNight() && mThemeManager.isDarkModeEnabled()));
    }

    private static boolean isNight() {
        return (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
    }

}
