package com.hardcodecoder.pulsemusic.themes;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.R;

public class ThemeStore {

    public static final String DARK_THEME_CATEGORY = "Dark_themes_key";
    public static final String ACCENT_COLOR = "Accent_Color";

    static final short LIGHT_THEME = 515;

    public static final short DARK_THEME_GRAY = 616;
    public static final short DARK_THEME_KINDA = 626;
    public static final short DARK_THEME_PURE_BLACK = 636;

    public static final int CINNAMON = 10;
    public static final int GREEN = 11;
    public static final int OCEAN = 12;
    public static final int ORCHID = 13;
    public static final int BLUE = 14;
    public static final int PURPLE = 15;
    public static final int SPACE = 16;


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

    @StyleRes
    static int getAccentById(boolean darkModeOn, int id) {
        if (darkModeOn) {
            switch (id) {
                case CINNAMON:
                    return R.style.CinnamonDark;
                case GREEN:
                    return R.style.GreenDark;
                case OCEAN:
                    return R.style.OceanDark;
                case ORCHID:
                    return R.style.OrchidDark;
                case BLUE:
                    return R.style.BlueDark;
                case SPACE:
                    return R.style.SpaceDark;
                case PURPLE:
                default:
                    return R.style.PurpleDark;
            }
        } else {
            switch (id) {
                case CINNAMON:
                    return R.style.Cinnamon;
                case GREEN:
                    return R.style.Green;
                case OCEAN:
                    return R.style.Ocean;
                case ORCHID:
                    return R.style.Orchid;
                case BLUE:
                    return R.style.Blue;
                case SPACE:
                    return R.style.Space;
                case PURPLE:
                default:
                    return R.style.Purple;
            }
        }
    }
}
