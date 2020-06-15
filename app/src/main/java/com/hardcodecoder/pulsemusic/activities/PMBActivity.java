package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;

public abstract class PMBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ThemeManagerUtils.getThemeToApply());
        getTheme().applyStyle(ThemeManagerUtils.getAccentStyleToApply(), true);
        super.onCreate(savedInstanceState);
    }
}
