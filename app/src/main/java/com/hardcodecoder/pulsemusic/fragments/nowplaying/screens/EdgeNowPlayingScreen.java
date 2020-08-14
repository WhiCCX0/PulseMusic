package com.hardcodecoder.pulsemusic.fragments.nowplaying.screens;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.bumptech.glide.GenericTransitionOptions;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.nowplaying.base.BaseNowPlayingScreen;

public class EdgeNowPlayingScreen extends BaseNowPlayingScreen {

    public static final String TAG = EdgeNowPlayingScreen.class.getSimpleName();
    private AppCompatSeekBar mProgressSeekBar;
    private ImageView mAlbumCover;
    private ImageView mFavoriteBtn;
    private ImageView mRepeatBtn;
    private ImageView mPlayPauseBtn;
    private MaterialTextView mTitle;
    private MaterialTextView mStartTime;
    private MaterialTextView mEndTime;
    private MaterialTextView mSubTitle;
    private MaterialTextView mUpNext;

    public static EdgeNowPlayingScreen getInstance() {
        return new EdgeNowPlayingScreen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing_edge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAlbumCover = view.findViewById(R.id.edge_nps_album_cover);
        mTitle = view.findViewById(R.id.edge_nps_title);
        mSubTitle = view.findViewById(R.id.edge_nps_sub_title);
        mProgressSeekBar = view.findViewById(R.id.edge_nps_seek_bar);
        mStartTime = view.findViewById(R.id.edge_nps_start_time);
        mEndTime = view.findViewById(R.id.edge_nps_end_time);
        mRepeatBtn = view.findViewById(R.id.edge_nps_repeat_btn);
        mPlayPauseBtn = view.findViewById(R.id.edge_nps_play_pause_btn);
        mFavoriteBtn = view.findViewById(R.id.edge_nps_favourite_btn);
        mUpNext = view.findViewById(R.id.edge_nps_up_next);
        view.findViewById(R.id.edge_nps_close_btn).setOnClickListener(v -> {
            if (null != getActivity())
                getActivity().finish();
        });
        setUpSeekBarControls(mProgressSeekBar);
        setUpSkipControls(
                view.findViewById(R.id.edge_nps_prev_btn),
                view.findViewById(R.id.edge_nps_next_btn));
        mRepeatBtn.setOnClickListener(v -> toggleRepeatMode());
        mPlayPauseBtn.setOnClickListener(v -> togglePlayPause());
        mFavoriteBtn.setOnClickListener(v -> toggleFavorite());
    }

    @Override
    public void onMetadataDataChanged(MediaMetadata metadata) {
        super.onMetadataDataChanged(metadata);
        if (null == metadata) return;
        long seconds = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000;
        mProgressSeekBar.setProgress(0);
        mProgressSeekBar.setMax((int) seconds);
        mStartTime.setText(getFormattedElapsedTime(0));
        mEndTime.setText(getFormattedElapsedTime(seconds));
        mSubTitle.setText(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
        mTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));

        GlideApp.with(this)
                .load(metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
                .transition(GenericTransitionOptions.with(R.anim.now_playing_album_card))
                .into(mAlbumCover);
        mUpNext.setText(getUpNextText());
    }

    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        super.onPlaybackStateChanged(state);
        if (state == null) return;
        togglePlayPauseAnimation(mPlayPauseBtn, state);
    }


    @Override
    public void onRepeatStateChanged(boolean repeat) {
        mRepeatBtn.setImageResource(repeat ? R.drawable.ic_repeat_one : R.drawable.ic_repeat);
    }

    @Override
    public void onFavoriteStateChanged(boolean isFavorite) {
        mFavoriteBtn.setSelected(isFavorite);
    }


    @Override
    public void onProgressValueChanged(int progressInSec) {
        mProgressSeekBar.setProgress(progressInSec);
        mStartTime.setText(getFormattedElapsedTime(progressInSec));
    }
}