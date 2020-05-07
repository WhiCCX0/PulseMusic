package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.ThemeChooserBottomSheetDialogFragment;
import com.hardcodecoder.pulsemusic.themes.ThemeManager;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;


public class ThemeActivity extends PMBActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_theme);

        findViewById(R.id.laf_select_dark_theme).setOnClickListener(v -> {
            ThemeChooserBottomSheetDialogFragment dialog = ThemeChooserBottomSheetDialogFragment.getInstance();
            dialog.show(getSupportFragmentManager(), ThemeChooserBottomSheetDialogFragment.TAG);
        });

        // Get custom views
        SettingsToggleableItem enableDarkThemeLayout = findViewById(R.id.laf_enable_dark_mode);
        SwitchMaterial enableDarkThemeSwitch = enableDarkThemeLayout.findViewById(R.id.setting_toggleable_item_switch);

        SettingsToggleableItem enableAutoThemeLayout = findViewById(R.id.laf_enable_auto_mode);
        SwitchMaterial switchMaterialEnableAutoMode = enableAutoThemeLayout.findViewById(R.id.setting_toggleable_item_switch);


        // Configure state of views based on saved settings
        boolean darkModeEnable = AppSettings.isDarkModeEnabled(this);
        enableDarkThemeSwitch.setChecked(darkModeEnable);

        boolean autoModeEnable = AppSettings.isAutoThemeEnabled(this);
        switchMaterialEnableAutoMode.setChecked(autoModeEnable);
        enableDarkThemeSwitch.setEnabled(!autoModeEnable);
        enableDarkThemeLayout.setEnabled(!autoModeEnable);

        //Add listeners to switch views
        enableDarkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ThemeManagerUtils.toggleDarkTheme(this, isChecked))
                applyTheme();
        });
        enableDarkThemeLayout.setOnClickListener(v -> {
            // Trigger switch enable/disable
            enableDarkThemeSwitch.setChecked(!enableDarkThemeSwitch.isChecked());
        });

        switchMaterialEnableAutoMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            enableDarkThemeSwitch.setEnabled(!isChecked);
            if (ThemeManagerUtils.toggleAutoTheme(this, isChecked)) {
                applyTheme();
            } else {
                // User does not want auto theme based on time of day
                // Revert to theme selected via darkThemeToggle
                if (enableDarkThemeSwitch.isChecked() && !ThemeManager.isDarkModeEnabled()) {
                    // User previously select dark theme so when auto theme is
                    // disabled apply dark theme if not already applied
                    applyTheme();
                } else if (!enableDarkThemeSwitch.isChecked() && ThemeManager.isDarkModeEnabled()) {
                    // User previously select light theme so when auto theme is
                    // disabled apply light theme if not already applied
                    applyTheme();
                }
            }
        });
        enableAutoThemeLayout.setOnClickListener(v -> {
            // Trigger switch enable/disable
            switchMaterialEnableAutoMode.setChecked(!switchMaterialEnableAutoMode.isChecked());
        });
    }

    private void applyTheme() {
        if (ThemeManagerUtils.initialiseThemeManager(this)) {
            startActivity(new Intent(this, ThemeActivity.class));
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
            finish();
        }
    }
}
