package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class AlbumCardStyleChooser extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "AlbumCardStyleChooser";
    private boolean mOptionChanged = false;

    public static AlbumCardStyleChooser getInstance() {
        return new AlbumCardStyleChooser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_album_card_style, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView title = view.findViewById(R.id.selector_title);
        title.setText(getString(R.string.now_playing_selector_album_style_title));
        if (null != getContext()) {

            RadioGroup radioGroup = view.findViewById(R.id.selector_radio_button_group);
            int currentStyle = AppSettings.getNowPlayingAlbumCardStyle(getContext());

            switch (currentStyle) {
                case Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_SQUARE:
                    ((RadioButton) radioGroup.findViewById(R.id.selector_album_style_square)).setChecked(true);
                    break;
                case Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_CIRCLE:
                    ((RadioButton) radioGroup.findViewById(R.id.selector_album_style_circle)).setChecked(true);
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> mOptionChanged = true);

            view.findViewById(R.id.selector_set_btn).setOnClickListener(v1 -> {
                if (mOptionChanged) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.selector_album_style_square:
                            AppSettings.setNowPlayingAlbumCardStyle(getContext(), Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_SQUARE);
                            break;
                        case R.id.selector_album_style_circle:
                            AppSettings.setNowPlayingAlbumCardStyle(getContext(), Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_CIRCLE);
                            break;
                    }
                }
                dismiss();
            });
        }
    }
}
