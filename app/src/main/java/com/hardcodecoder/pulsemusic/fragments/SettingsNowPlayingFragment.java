package com.hardcodecoder.pulsemusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.R;

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
    }
}
