package com.hardcodecoder.pulsemusic.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.BuildConfig;
import com.hardcodecoder.pulsemusic.R;

public class SettingsAboutFragment extends Fragment {

    public static final String TAG = "SettingsAboutFragment";

    public static SettingsAboutFragment getInstance() {
        return new SettingsAboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialTextView tempTextView = view.findViewById(R.id.about_app_version);
        tempTextView.setText(BuildConfig.VERSION_NAME);

        tempTextView = view.findViewById(R.id.about_app_release_date);
        tempTextView.setText(BuildConfig.BUILD_DATE);

        tempTextView = view.findViewById(R.id.about_app_build_code);
        tempTextView.setText(String.valueOf(BuildConfig.VERSION_CODE));

        tempTextView = view.findViewById(R.id.about_app_build_type);
        tempTextView.setText(BuildConfig.BUILD_TYPE);

        view.findViewById(R.id.about_card_github_link).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.source_code_link)));
            startActivity(i);
        });
    }
}
