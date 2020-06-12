package com.hardcodecoder.pulsemusic.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.GenericTransitionOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.StorageHelper;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NowPlayingActivity extends MediaSessionActivity {

    private final Handler mHandler = new Handler();
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ImageView mFavBtn;
    private ImageView mRepeatBtn;
    private ImageView mPlayPause;
    private SeekBar seekBar;
    private MaterialTextView toolbarSongTitle;
    private MaterialTextView startTime;
    private MaterialTextView endTime;
    private MaterialTextView artistAlbums;
    private MediaController mController;
    private PlaybackState mState;
    private TrackManager tm;
    private int progress = 0;
    private boolean isTrackFavorite = false;
    private boolean animateSeekBar = false;
    private final Runnable mUpdateProgressTask = this::updateProgress;
    /*
     * Components for seek bar progress update
     */
    private ScheduledFuture<?> mScheduleFuture;

    private final MediaController.Callback mCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            if (null != state) {
                mState = state;
                updatePlaybackState();
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            stopSeekBarUpdate();
            updateMetaData(metadata);
            updateFavoriteButtonState();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        tm = TrackManager.getInstance();
        /*
         * Referencing Views
         */
        startTime = findViewById(R.id.activity_np_start_time);
        endTime = findViewById(R.id.activity_np_end_time);
        seekBar = findViewById(R.id.activity_np_seekBar);
        mPlayPause = findViewById(R.id.activity_np_play_pause_btn);
        artistAlbums = findViewById(R.id.activity_np_album_artist_name);
        toolbarSongTitle = findViewById(R.id.activity_np_song_title);
        mFavBtn = findViewById(R.id.activity_np_favourite_btn);
        mRepeatBtn = findViewById(R.id.activity_np_btn_repeat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) animateSeekBar = true;

        int albumStyle = AppSettings.getNowPlayingAlbumCardStyle(this);
        boolean showOverlay = AppSettings.isNowPlayingAlbumCardOverlayEnabled(this);
        MaterialCardView mAlbumCard = findViewById(R.id.activity_np_album_art_card);

        mAlbumCard.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (albumStyle == Preferences.NOW_PLAYING_ALBUM_CARD_STYLE_CIRCLE) {
                float side = mAlbumCard.getMeasuredWidthAndState();
                mAlbumCard.setRadius(side / 2);
            }
        });

        if (showOverlay) {
            mAlbumCard.setCardElevation(0);
            ((ViewStub) mAlbumCard.findViewById(R.id.stub_album_art_style_overlay)).inflate();
        }
        initButtons();
    }

    private void initButtons() {
        findViewById(R.id.activity_np_skip_next_btn).setOnClickListener(v -> mController.getTransportControls().skipToNext());
        findViewById(R.id.activity_np_skip_prev_btn).setOnClickListener(v -> mController.getTransportControls().skipToPrevious());
        findViewById(R.id.activity_np_close_btn).setOnClickListener(v -> onBackPressed());
        mFavBtn.setOnClickListener(v -> {
            if (isTrackFavorite) removeFromFavorite();
            else addToFavorite();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                startTime.setText(DateUtils.formatElapsedTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekBarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress = seekBar.getProgress();
                mController.getTransportControls().seekTo(progress);
            }
        });

        updateFavoriteButtonState();
        updateRepeatBtn();
    }

    private void addToFavorite() {
        StorageHelper.addTrackToFavorites(this, tm.getActiveQueueItem());
        updateFavoriteButtonState();
        Toast.makeText(this, getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite() {
        StorageHelper.removeTrackFromFavorites(this, tm.getActiveQueueItem());
        updateFavoriteButtonState();
        Toast.makeText(this, getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
    }

    private void updateRepeatBtn() {
        mRepeatBtn.setImageResource(tm.isCurrentTrackInRepeatMode() ? R.drawable.ic_repeat_one : R.drawable.ic_repeat);
        mRepeatBtn.setOnClickListener(v -> {
            boolean b = tm.isCurrentTrackInRepeatMode();
            mRepeatBtn.setImageResource(b ? R.drawable.ic_repeat : R.drawable.ic_repeat_one);
            tm.repeatCurrentTrack(!b);
            if (b)
                Toast.makeText(this, getString(R.string.repeat_disabled), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.repeat_enabled), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateFavoriteButtonState() {
        boolean value = StorageHelper.isTrackAlreadyInFavorites(tm.getActiveQueueItem());
        mFavBtn.setSelected(value);
        isTrackFavorite = value;
    }

    private void updateMetaData(MediaMetadata metadata) {
        if (metadata != null) {
            mHandler.postDelayed(() ->
                            GlideApp.with(this)
                                    .load(metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
                                    .transition(GenericTransitionOptions.with(R.anim.now_playing_album_card))
                                    .into((ImageView) findViewById(R.id.activity_np_album_art))
                    , 240);

            long sec = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000;
            seekBar.setProgress(progress = 0);
            seekBar.setMax((int) sec);
            endTime.setText(DateUtils.formatElapsedTime(sec));

            artistAlbums.setText(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));

            toolbarSongTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));
            toolbarSongTitle.setSelected(true);
            updateRepeatBtn();
        }
    }

    private void updatePlaybackState() {
        switch (mState.getState()) {
            case PlaybackState.STATE_PLAYING:
                scheduleSeekBarUpdate();
                break;

            case PlaybackState.STATE_STOPPED:
                stopSeekBarUpdate();
                progress = 0;
                seekBar.setProgress(progress);
                break;

            case PlaybackState.STATE_PAUSED:
                stopSeekBarUpdate();
                --progress;
                break;

            case PlaybackState.STATE_BUFFERING:
            case PlaybackState.STATE_CONNECTING:
            case PlaybackState.STATE_ERROR:
            case PlaybackState.STATE_FAST_FORWARDING:
            case PlaybackState.STATE_NONE:
            case PlaybackState.STATE_REWINDING:
            case PlaybackState.STATE_SKIPPING_TO_NEXT:
            case PlaybackState.STATE_SKIPPING_TO_PREVIOUS:
            case PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM:
                break;
        }
        updatePlayButton();
    }

    private void updatePlayButton() {
        if (null != mState) {
            if (mState.getState() == PlaybackState.STATE_PLAYING)
                mPlayPause.setImageResource(R.drawable.pause_to_play);
            else mPlayPause.setImageResource(R.drawable.play_to_pause_linear_out_slow_in);
        }
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
        mController = controller;
        mController.registerCallback(mCallback);
        mState = mController.getPlaybackState();
        if (null != mState) {
            // Make call to updateMetadata before calling updatePlaybackState
            // updateMetadata reset's seek bar position
            updateMetaData(mController.getMetadata());

            progress = (int) mState.getPosition() / 1000;
            setSeekBarProgress();

            //Keep this block here, moving it to onCreate can
            //create issues if this gets executed before onConnected
            //onClick listener will not be set due to null state
            //mState variable gets updated from onPlaybackStateChanged hence
            //latest state is check before invoking any action
            mPlayPause.setOnClickListener(v -> {
                if (mState.getState() == PlaybackState.STATE_PLAYING) {
                    mController.getTransportControls().pause();
                    Drawable d = getDrawable(R.drawable.pause_to_play);
                    mPlayPause.setImageDrawable(d);
                    if (d instanceof AnimatedVectorDrawable) ((AnimatedVectorDrawable) d).start();
                } else {
                    mController.getTransportControls().play();
                    Drawable d = getDrawable(R.drawable.play_to_pause_linear_out_slow_in);
                    mPlayPause.setImageDrawable(d);
                    if (d instanceof AnimatedVectorDrawable) ((AnimatedVectorDrawable) d).start();
                }
            });
            updatePlaybackState();
            updatePlayButton();
        }
    }

    private void scheduleSeekBarUpdate() {
        if (seekBar.getProgress() == seekBar.getMax())
            seekBar.setProgress(progress = 0);
        if (!mExecutorService.isShutdown()) {
            if (null != mScheduleFuture)
                mScheduleFuture.cancel(true);
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(() -> mHandler.post(mUpdateProgressTask), 500, 1000, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekBarUpdate() {
        if (mScheduleFuture != null)
            mScheduleFuture.cancel(true);
        if (seekBar.getProgress() == seekBar.getMax())
            seekBar.setProgress(progress = 0);
    }

    private void updateProgress() {
        progress++;
        setSeekBarProgress();
    }

    @SuppressLint("NewApi")
    private void setSeekBarProgress() {
        if (animateSeekBar) seekBar.setProgress(progress, true);
        else seekBar.setProgress(progress);
    }

    @Override
    protected void onStop() {
        mController.unregisterCallback(mCallback);
        stopSeekBarUpdate();
        if (!mExecutorService.isShutdown())
            mExecutorService.shutdown();
        super.onStop();
    }
}
