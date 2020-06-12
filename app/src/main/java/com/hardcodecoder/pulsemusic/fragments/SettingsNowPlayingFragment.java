package com.hardcodecoder.pulsemusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.dialog.AlbumCardStyleChooser;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.SettingsToggleableItem;

import java.util.Objects;

public class SettingsNowPlayingFragment extends Fragment {

    public static final String TAG = "SettingsNowPlayingFragment";

    public static SettingsNowPlayingFragment getInstance() {
        return new SettingsNowPlayingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SettingsFragmentsListener mListener = (SettingsFragmentsListener) getActivity();
        if (mListener instanceof SettingsActivity)
            mListener.setToolbarTitle(R.string.now_playing_title);

        view.findViewById(R.id.now_playing_album_style).setOnClickListener(v -> {
            AlbumCardStyleChooser dialog = AlbumCardStyleChooser.getInstance();
            dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), AlbumCardStyleChooser.TAG);
        });

        if (null != getContext()) {
            boolean enabled = AppSettings.isNowPlayingAlbumCardOverlayEnabled(getContext());

            SettingsToggleableItem albumCardDecorationLayout = view.findViewById(R.id.now_playing_album_decoration);
            SwitchMaterial albumCardDecorationSwitch = albumCardDecorationLayout.findViewById(R.id.setting_toggleable_item_switch);

            albumCardDecorationSwitch.setChecked(enabled);
            albumCardDecorationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                    AppSettings.setNowPlayingAlbumCardOverlayEnabled(getContext(), isChecked));
            albumCardDecorationLayout.setOnClickListener(v ->
                    albumCardDecorationSwitch.setChecked(!albumCardDecorationSwitch.isChecked()));
        }
    }
}
