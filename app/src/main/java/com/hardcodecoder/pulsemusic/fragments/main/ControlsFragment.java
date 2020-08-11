package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.nowplaying.NowPlayingScreenActivity;

public class ControlsFragment extends Fragment {

    private MaterialTextView tv1;
    private ImageView playPause;
    private MediaController mController;
    private MediaController.TransportControls mTransportControl;
    private PlaybackState mState;

    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            mState = state;
            updateControls();
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            updateMetadata(metadata);
        }
    };

    public ControlsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controls, container, false);
    }

    @Override
    public void onStart() {
        updateController();
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        tv1 = v.findViewById(R.id.song_name);
        tv1.setSelected(true);
        playPause = v.findViewById(R.id.cf_play_pause_btn);
        ImageView skipNext = v.findViewById(R.id.cf_skip_next_btn);
        ImageView skipPrev = v.findViewById(R.id.cf_skip_prev_btn);

        playPause.setOnClickListener(v1 -> {
            if (mState.getState() == PlaybackState.STATE_PLAYING)
                mTransportControl.pause();
            else
                mTransportControl.play();
        });

        skipNext.setOnClickListener(v1 -> mTransportControl.skipToNext());

        skipPrev.setOnClickListener(v1 -> mTransportControl.skipToPrevious());

        v.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), NowPlayingScreenActivity.class);
            startActivity(intent);
        });
    }

    private void updateControls() {
        if (null == mState)
            return;
        if (mState.getState() == PlaybackState.STATE_PLAYING)
            playPause.setImageResource(R.drawable.ic_round_pause);
        else
            playPause.setImageResource(R.drawable.ic_round_play);
    }

    private void updateMetadata(MediaMetadata metadata) {
        if (metadata != null)
            tv1.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
    }

    private void updateController() {
        if (mController == null && getActivity() != null)
            mController = getActivity().getMediaController();
        if (mController != null) {
            mController.registerCallback(mCallback);
            mState = mController.getPlaybackState();
            if (mTransportControl == null)
                mTransportControl = mController.getTransportControls();
            updateMetadata(mController.getMetadata());
            updateControls();
        }
    }

    @Override
    public void onStop() {
        if (mController != null)
            mController.unregisterCallback(mCallback);
        super.onStop();
    }
}
