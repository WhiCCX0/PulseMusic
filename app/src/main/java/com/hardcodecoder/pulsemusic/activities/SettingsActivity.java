package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.hardcodecoder.pulsemusic.R;


public class SettingsActivity extends PMBActivity {

    /*private static final String TAG = "SettingsActivity";
    private boolean autoModeEnable;
    private boolean darkModeEnable;
    private boolean optionChanged = false;
    private boolean isAccentChanged = false;
    private MaterialToolbar mToolbar;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setUpDefaultStates();
        //findViewById(R.id.settings_close_btn).setOnClickListener(v -> finish());
        MaterialToolbar mToolbar = findViewById(R.id.material_toolbar);
        setSupportActionBar(mToolbar);


        /*mToolbar.setNavigationOnClickListener(v -> backTrackFragments());
        if(null == savedInstanceState)
            swapFragment(SettingsMainFragment.TAG);*/

        findViewById(R.id.themeSettings).setOnClickListener(v -> startActivity(new Intent(this, ThemeActivity.class)));
        findViewById(R.id.nowPlayingSettings).setOnClickListener(v -> {
        });
        findViewById(R.id.contributorsSettings).setOnClickListener(v -> {
        });
        findViewById(R.id.aboutSettings).setOnClickListener(v -> {
        });
    }

    /*private void setToolbarTitle(String title){
        mToolbar.setTitle(title);
    }*/

    /*@Override
    public void changeFragment(String fragmentTag) {
        Log.e(TAG, "Received callback to changed fragment");
        swapFragment(fragmentTag);
    }

    private void swapFragment(String fragmentTag){
        String title = "";
        switch (fragmentTag){
            case SettingsMainFragment.TAG:
                switchToFragment = SettingsMainFragment.getInstance();
                title= SettingsMainFragment.TITLE;
                break;
            case SettingsThemeFragment.TAG:
                switchToFragment = SettingsThemeFragment.getInstance();
                title = SettingsThemeFragment.TITLE;
                break;
        }
        if(null != switchToFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(switchToFragment, fragmentTag)
                    .replace(R.id.settings_container, switchToFragment, fragmentTag)
                    .addToBackStack(SettingsMainFragment.TAG)
                    .commit();
            setToolbarTitle(title);
        }
    }

    private void backTrackFragments(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
            return;
        }
        getSupportFragmentManager().popBackStack();
    }*/

    /*private void setUpDefaultStates() {
        TextView darkModeTextView = findViewById(R.id.setting_switch_1_title);
        Switch darkThemeToggle = findViewById(R.id.settings_toggle_dark_theme);
        TextView autoThemeTextView = findViewById(R.id.settings_switch_2_title);
        Switch autoThemeToggle = findViewById(R.id.settings_toggle_auto_theme);

        darkModeEnable = AppSettings.isDarkModeEnabled(this);
        darkModeTextView.setText(darkModeEnable ? R.string.dark_on : R.string.light_on);
        darkThemeToggle.setChecked(darkModeEnable);

        autoModeEnable = AppSettings.isAutoThemeEnabled(this);
        autoThemeTextView.setText(autoModeEnable ? R.string.auto_theme_enabled : R.string.auto_theme_disabled);
        autoThemeToggle.setChecked(autoModeEnable);
        darkThemeToggle.setEnabled(!autoModeEnable);
        findViewById(R.id.setting_switch_1_title).setEnabled(!autoModeEnable);


        darkThemeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeManager.enableDarkMode(this, isChecked);

            if (!ThemeManager.isDarkModeEnabled() && isChecked) {
                // User wants Dark theme but Light theme is previously
                // applied so restart the activity to apply Dark theme
                restart();
            } else if (ThemeManager.isDarkModeEnabled() && !isChecked) {
                // User wants Light theme but dark theme is previously
                // applied so restart the activity to apply Light theme
                restart();
            }
        });

        autoThemeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeManager.enableAutoTheme(this, isChecked);
            darkThemeToggle.setEnabled(!isChecked);
            findViewById(R.id.setting_switch_1_title).setEnabled(!isChecked);

            if (isChecked && needToToggleTheme()) {
                // User want auto theme based on time of day
                // If its night/day and current theme is light/dark respectively
                // them restart to apply new theme
                restart();
            } else {
                // User does not want auto theme based on time of day
                // Revert to theme selected via darkThemeToggle
                if (darkThemeToggle.isChecked() && !ThemeManager.isDarkModeEnabled()) {
                    // User previously select dark theme so when auto theme is
                    // disabled apply dark theme if not already applied
                    restart();
                } else if (!darkThemeToggle.isChecked() && ThemeManager.isDarkModeEnabled()) {
                    // User previously select light theme so when auto theme is
                    // disabled apply light theme if not already applied
                    restart();
                }
            }
        });

        findViewById(R.id.accents_options).setOnClickListener(this::openAccentPicker);
        findViewById(R.id.dark_theme_options).setOnClickListener(this::openDarkThemeSelector);
    }*/

    /*private void openAccentPicker(View view) {
        View windowView = View.inflate(this, R.layout.accent_color_picker, null);
        PopupWindow window = new PopupWindow(windowView, RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT, true);
        RadioGroup radioGroup = windowView.findViewById(R.id.radio_group);

        int currentAccent = AppSettings.getSelectedAccentColor(this);
        switch (currentAccent) {
            case ThemeStore.CINNAMON:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_1)).setChecked(true);
                break;
            case ThemeStore.GREEN:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_2)).setChecked(true);
                break;
            case ThemeStore.OCEAN:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_3)).setChecked(true);
                break;
            case ThemeStore.ORCHID:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_4)).setChecked(true);
                break;
            case ThemeStore.BLUE:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_5)).setChecked(true);
                break;
            case ThemeStore.PURPLE:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_6)).setChecked(true);
                break;
            case ThemeStore.SPACE:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_7)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> isAccentChanged = true);

        windowView.findViewById(R.id.btn_set).setOnClickListener(v -> {
            if (isAccentChanged) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rd_btn_1:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.CINNAMON);
                        break;
                    case R.id.rd_btn_2:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.GREEN);
                        break;
                    case R.id.rd_btn_3:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.OCEAN);
                        break;
                    case R.id.rd_btn_4:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.ORCHID);
                        break;
                    case R.id.rd_btn_5:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.BLUE);
                        break;
                    case R.id.rd_btn_6:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.PURPLE);
                        break;
                    case R.id.rd_btn_7:
                        ThemeManager.setSelectedAccentColor(this, ThemeStore.SPACE);
                        break;
                }
                isAccentChanged = false;
                if (window.isShowing())
                    window.dismiss();
                restart();
            }
        });

        window.setBackgroundDrawable(getDrawable(R.drawable.popup_menu_background));
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBackgroundOnPopupWindow(window);
    }*/

   /* private void openDarkThemeSelector(View view) {
        View windowView = View.inflate(this, R.layout.dark_theme_picker, null);
        PopupWindow window = new PopupWindow(windowView, RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT, true);

        RadioGroup radioGroup = windowView.findViewById(R.id.radio_group);

        int currentTheme = AppSettings.getSelectedDarkTheme(this);

        switch (currentTheme) {
            case ThemeStore.DARK_THEME_1:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_1)).setChecked(true);
                break;
            case ThemeStore.DARK_THEME_2:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_2)).setChecked(true);
                break;
            case ThemeStore.DARK_THEME_3:
                ((RadioButton) radioGroup.findViewById(R.id.rd_btn_3)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> optionChanged = true);

        windowView.findViewById(R.id.btn_set).setOnClickListener(v1 -> {
            if (window.isShowing())
                window.dismiss();
            if (optionChanged) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rd_btn_1:
                        ThemeManager.setSelectedDarkTheme(this, ThemeStore.DARK_THEME_1);
                        break;
                    case R.id.rd_btn_2:
                        ThemeManager.setSelectedDarkTheme(this, ThemeStore.DARK_THEME_2);
                        break;
                    case R.id.rd_btn_3:
                        ThemeManager.setSelectedDarkTheme(this, ThemeStore.DARK_THEME_3);
                        break;
                }

                if (autoModeEnable && isNight()) restart();
                else if (darkModeEnable) restart();
                optionChanged = false;
            }
        });

        window.setBackgroundDrawable(getDrawable(R.drawable.popup_menu_background));
        window.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBackgroundOnPopupWindow(window);
    }

    private void dimBackgroundOnPopupWindow(PopupWindow window) {
        View container;
        if (null == window.getBackground()) container = (View) window.getContentView().getParent();
        else container = (View) window.getContentView().getParent().getParent();
        WindowManager wm = (WindowManager) window.getContentView().getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.70f;
        if (null != wm)
            wm.updateViewLayout(container, p);
    }

    private boolean needToToggleTheme() {
        return ((isNight() && !ThemeManager.isDarkModeEnabled()) || (!isNight() && ThemeManager.isDarkModeEnabled()));
    }

    private boolean isNight() {
        return (DayTimeUtils.getTimeOfDay() == DayTimeUtils.DayTime.NIGHT);
    }

    private void restart() {
        ThemeManager.init(getApplicationContext());
        startActivity(new Intent(this, SettingsActivity.class));
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        finish();
    }*/

}
