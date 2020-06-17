package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class ThemeChooserBottomSheetDialogFragment extends RoundedBottomSheetDialogFragment {


    public static final String TAG = "ThemeChooserBottomSheetDialog";
    private boolean mOptionChanged = false;
    private Context mContext;

    public static ThemeChooserBottomSheetDialogFragment getInstance() {
        return new ThemeChooserBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_choose_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        SettingsFragmentsListener mListener = (SettingsFragmentsListener) getActivity();
        RadioGroup radioGroup = view.findViewById(R.id.radio_button_group);
        int currentTheme = AppSettings.getSelectedDarkTheme(mContext);

        switch (currentTheme) {
            case Preferences.DARK_THEME_GRAY:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_dark_theme_gray)).setChecked(true);
                break;
            case Preferences.DARK_THEME_KINDA:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_dark_theme_kinda)).setChecked(true);
                break;
            case Preferences.DARK_THEME_PURE_BLACK:
                ((RadioButton) radioGroup.findViewById(R.id.radio_btn_dark_theme_pure_black)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

        view.findViewById(R.id.choose_theme_set_btn).setOnClickListener(v1 -> {
            if (mOptionChanged) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radio_btn_dark_theme_gray:
                        ThemeManagerUtils.setSelectedDarkTheme(mContext, Preferences.DARK_THEME_GRAY);
                        break;
                    case R.id.radio_btn_dark_theme_kinda:
                        ThemeManagerUtils.setSelectedDarkTheme(mContext, Preferences.DARK_THEME_KINDA);
                        break;
                    case R.id.radio_btn_dark_theme_pure_black:
                        ThemeManagerUtils.setSelectedDarkTheme(mContext, Preferences.DARK_THEME_PURE_BLACK);
                        break;
                }
                /*if (ThemeManagerUtils.needToApplyNewDarkTheme()) {
                    if (null != getActivity()) {
                        getActivity().recreate();
                    }
                }*/
                if (ThemeManagerUtils.needToApplyNewDarkTheme()) {
                    //Theme need to be updated
                    if (mListener instanceof SettingsActivity)
                        mListener.onThemeChanged();
                }
            }
            dismiss();
        });

        view.findViewById(R.id.choose_theme_cancel_btn).setOnClickListener(v -> dismiss());
    }
}
