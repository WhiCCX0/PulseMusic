package com.hardcodecoder.pulsemusic.dialog;

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
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class NowPlayingStyleChooser extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "AlbumCardStyleChooser";
    private boolean mOptionChanged = false;

    public static NowPlayingStyleChooser getInstance() {
        return new NowPlayingStyleChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_now_playing_screen_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (null != getContext()) {
            RadioGroup radioGroup = view.findViewById(R.id.selector_radio_button_group);
            int currentStyle = AppSettings.getNowPlayingScreenStyle(getContext());

            switch (currentStyle) {
                case Preferences.NOW_PLAYING_SCREEN_MODERN:
                    ((RadioButton) radioGroup.findViewById(R.id.selector_now_playing_screen_modern)).setChecked(true);
                    break;
                case Preferences.NOW_PLAYING_SCREEN_STYLISH:
                    ((RadioButton) radioGroup.findViewById(R.id.selector_now_playing_screen_stylish)).setChecked(true);
                    break;
                case Preferences.NOW_PLAYING_SCREEN_EDGE:
                    ((RadioButton) radioGroup.findViewById(R.id.selector_now_playing_screen_edge)).setChecked(true);
                    break;
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

            view.findViewById(R.id.selector_set_btn).setOnClickListener(v1 -> {
                if (mOptionChanged) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.selector_now_playing_screen_modern:
                            AppSettings.setNowPlayingScreenStyle(getContext(), Preferences.NOW_PLAYING_SCREEN_MODERN);
                            break;
                        case R.id.selector_now_playing_screen_stylish:
                            AppSettings.setNowPlayingScreenStyle(getContext(), Preferences.NOW_PLAYING_SCREEN_STYLISH);
                            break;
                        case R.id.selector_now_playing_screen_edge:
                            AppSettings.setNowPlayingScreenStyle(getContext(), Preferences.NOW_PLAYING_SCREEN_EDGE);
                            break;
                    }
                }
                dismiss();
            });

            view.findViewById(R.id.selector_cancel_btn).setOnClickListener(v -> dismiss());
        }
    }
}
