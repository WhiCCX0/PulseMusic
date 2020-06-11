package com.hardcodecoder.pulsemusic.interfaces;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public interface SettingsFragmentsListener {

    void changeFragment(Fragment fragment);

    void onThemeChanged();

    void setToolbarTitle(@StringRes int titleId);
}
