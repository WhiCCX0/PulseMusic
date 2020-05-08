package com.hardcodecoder.pulsemusic.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.GlideConstantArtifacts;
import com.hardcodecoder.pulsemusic.R;

public class SettingsContributorsFragment extends Fragment {


    public static final String TAG = "SettingsContributorsFragment";

    public static SettingsContributorsFragment getInstance() {
        return new SettingsContributorsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_contributors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.github_logo).setOnClickListener(v -> openLink(R.string.github_link));
        view.findViewById(R.id.facebook_logo).setOnClickListener(v -> openLink(R.string.facebook_link));
        view.findViewById(R.id.twitter_logo).setOnClickListener(v -> openLink(R.string.twitter_link));
        view.findViewById(R.id.telegram_logo).setOnClickListener(v -> openLink(R.string.telegram_link));

        GlideApp.with(view)
                .load(getString(R.string.profile_icon_link))
                .transform(GlideConstantArtifacts.getCircleCrop())
                .into((ImageView) view.findViewById(R.id.lead_developer_profile_icon));
    }

    private void openLink(@StringRes int linkId) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(getString(linkId)));
        startActivity(i);
    }
}
