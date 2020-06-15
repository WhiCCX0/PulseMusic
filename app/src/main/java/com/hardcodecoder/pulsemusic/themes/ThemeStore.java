package com.hardcodecoder.pulsemusic.themes;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.R;

public class ThemeStore {

    public static final String DARK_THEME_CATEGORY = "Dark_themes_key";

    static final short LIGHT_THEME = 515;
    public static final short DARK_THEME_GRAY = 616;
    public static final short DARK_THEME_KINDA = 626;
    public static final short DARK_THEME_PURE_BLACK = 636;

    public static final short ACCENT_EXODUS_FRUIT = 700;
    private static final short ACCENT_ELECTRON_BLUE = 701;
    private static final short ACCENT_MINT_LEAF = 702;
    private static final short ACCENT_CHI_GONG = 703;
    private static final short ACCENT_SEI_BAR = 704;

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
    static int getAccentById(int id, boolean desaturated) {
        switch (id) {
            case ACCENT_EXODUS_FRUIT:
                return desaturated ? R.style.ExodusFruitDesaturated : R.style.ExodusFruit;
            case ACCENT_ELECTRON_BLUE:
                return desaturated ? R.style.ElectronBlueDesaturated : R.style.ElectronBlue;
            case ACCENT_MINT_LEAF:
                return desaturated ? R.style.MintLeafDesaturated : R.style.MintLeaf;
            case ACCENT_CHI_GONG:
                return desaturated ? R.style.ChiGongDesaturated : R.style.ChiGong;
            case ACCENT_SEI_BAR:
                return desaturated ? R.style.SeiBarDesaturated : R.style.SeiBar;
            default:
                return R.style.ExodusFruit;
        }
    }
}
