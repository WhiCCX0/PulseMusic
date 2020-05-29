package com.hardcodecoder.pulsemusic.themes;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.R;

public class ThemeStore {

    public static final String DARK_THEME_CATEGORY = "Dark_themes_key";

    static final short LIGHT_THEME = 515;
    public static final short DARK_THEME_GRAY = 616;
    public static final short DARK_THEME_KINDA = 626;
    public static final short DARK_THEME_PURE_BLACK = 636;

    @StyleRes
    static int getThemeById(boolean darkModeOn, int id) {
        switch (id) {
            case DARK_THEME_GRAY:
                return R.style.ActivityThemeDark;
            case DARK_THEME_KINDA:
                return R.style.ActivityThemeKindaDark;
            case DARK_THEME_PURE_BLACK:
                return R.style.ActivityThemeBlack;
        }
        return darkModeOn ? R.style.ActivityThemeDark : R.style.ActivityThemeLight;
    }
}
