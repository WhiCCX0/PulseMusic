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
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;


public class SettingsMainFragment extends Fragment {

    public static final String TAG = "SettingsMainFragment";
    private SettingsFragmentsListener mListener;

    public static SettingsMainFragment getInstance() {
        return new SettingsMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mListener = (SettingsFragmentsListener) getActivity();
        view.findViewById(R.id.themeSettings).setOnClickListener(v -> openSettingsFragment(SettingsThemeFragment.getInstance()));
        view.findViewById(R.id.nowPlayingSettings).setOnClickListener(v -> openSettingsFragment(SettingsNowPlayingFragment.getInstance()));
        view.findViewById(R.id.contributorsSettings).setOnClickListener(v -> openSettingsFragment(SettingsContributorsFragment.getInstance()));
        view.findViewById(R.id.aboutSettings).setOnClickListener(v -> openSettingsFragment(SettingsAboutFragment.getInstance()));
    }

    private void openSettingsFragment(Fragment fragment) {
        if (mListener instanceof SettingsActivity) {
            mListener.changeFragment(fragment);
        }
    }
}
