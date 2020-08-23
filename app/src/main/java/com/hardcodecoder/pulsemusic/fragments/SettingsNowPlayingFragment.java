package com.hardcodecoder.pulsemusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.dialog.CornerRadiusChangeDialogFragment;
import com.hardcodecoder.pulsemusic.dialog.NowPlayingStyleChooser;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;

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

        view.findViewById(R.id.now_playing_screen_style).setOnClickListener(v -> {
            if (null != getActivity()) {
                NowPlayingStyleChooser dialog = NowPlayingStyleChooser.getInstance();
                dialog.show(getActivity().getSupportFragmentManager(), NowPlayingStyleChooser.TAG);
            }
        });

        view.findViewById(R.id.now_playing_album_cover_corner_radius).setOnClickListener(v -> {
            if (null != getActivity()) {
                CornerRadiusChangeDialogFragment dialog = CornerRadiusChangeDialogFragment.getInstance();
                dialog.show(getActivity().getSupportFragmentManager(), CornerRadiusChangeDialogFragment.TAG);
            }
        });
    }
}
