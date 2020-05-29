package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;

import com.hardcodecoder.pulsemusic.utils.DayTimeUtils;

public class ThemeManagerUtils {

    public static boolean toggleDarkTheme(Context context, boolean enabled) {
        ThemeManager.enableDarkMode(context, enabled);
        //return true if activity needs to restart because theme needs to be changed
        return (!ThemeManager.isDarkModeEnabled() && enabled) || (ThemeManager.isDarkModeEnabled() && !enabled);
    }

    public static boolean toggleAutoTheme(Context context, boolean enabled) {
        ThemeManager.enableAutoTheme(context, enabled);
        // User want auto theme based on time of day
        // If its night/day and current theme is light/dark respectively
        // them restart to apply new theme
        return (enabled && needToChangeTheme());

    }

    public static boolean needToApplyNewDarkTheme() {
        //Returns true to restart activity in order to apply new dark theme
        return (ThemeManager.isAutoModeEnabled() && isNight()) || ThemeManager.isDarkModeEnabled();
    }

    private static boolean needToChangeTheme() {
        return ((isNight() && !ThemeManager.isDarkModeEnabled()) || (!isNight() && ThemeManager.isDarkModeEnabled()));
    }

    private static boolean isNight() {
        return (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
    }
}
