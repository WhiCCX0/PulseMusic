package com.hardcodecoder.pulsemusic.interfaces;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public interface SettingsFragmentsListener {

    void changeFragment(Fragment fragment, @StringRes int titleId);

    void onThemeChanged();
}
