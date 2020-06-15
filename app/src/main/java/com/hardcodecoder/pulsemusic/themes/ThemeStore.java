package com.hardcodecoder.pulsemusic.themes;

import androidx.annotation.StyleRes;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;

class ThemeStore {

    @StyleRes
    static int getThemeById(boolean darkModeOn, int id) {
        switch (id) {
            case Preferences.DARK_THEME_GRAY:
                return R.style.ActivityThemeDark;
            case Preferences.DARK_THEME_KINDA:
                return R.style.ActivityThemeKindaDark;
            case Preferences.DARK_THEME_PURE_BLACK:
                return R.style.ActivityThemeBlack;
        }
        return darkModeOn ? R.style.ActivityThemeDark : R.style.ActivityThemeLight;
    }

    @StyleRes
    static int getAccentById(int id, boolean desaturated) {
        switch (id) {
            case Preferences.ACCENT_EXODUS_FRUIT:
                return desaturated ? R.style.ExodusFruitDesaturated : R.style.ExodusFruit;
            case Preferences.ACCENT_ELECTRON_BLUE:
                return desaturated ? R.style.ElectronBlueDesaturated : R.style.ElectronBlue;
            case Preferences.ACCENT_MINT_LEAF:
                return desaturated ? R.style.MintLeafDesaturated : R.style.MintLeaf;
            case Preferences.ACCENT_CHI_GONG:
                return desaturated ? R.style.ChiGongDesaturated : R.style.ChiGong;
            case Preferences.ACCENT_SEI_BAR:
                return desaturated ? R.style.SeiBarDesaturated : R.style.SeiBar;
            default:
                return R.style.ExodusFruit;
        }
    }
}
