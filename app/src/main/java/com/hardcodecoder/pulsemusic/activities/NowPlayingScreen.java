package com.hardcodecoder.pulsemusic.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
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
import com.hardcodecoder.pulsemusic.helper.MediaProgressUpdateHelper;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class NowPlayingScreen extends MediaSessionActivity implements MediaProgressUpdateHelper.Callback {

    private MediaProgressUpdateHelper mUpdateHelper;
    private MediaController.TransportControls mTransportControls;
    private ImageView mFavBtn;
    private ImageView mRepeatBtn;
    private ImageView mPlayPause;
    private SeekBar mSeekBar;
    private MaterialTextView mTrackTitle;
    private MaterialTextView mStartTime;
    private MaterialTextView mEndTime;
    private MaterialTextView mArtistText;
    private TrackManager mTrackManager;
    private PlaybackState mState;
    private boolean mCurrentTrackFavorite = false;
    private boolean mAnimateSeekBar = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_open_exit);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        mTrackManager = TrackManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) mAnimateSeekBar = true;
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
    }

    private void initializeViews() {
        /*
         * Referencing Views
         */
        mStartTime = findViewById(R.id.activity_np_start_time);
        mEndTime = findViewById(R.id.activity_np_end_time);
        mSeekBar = findViewById(R.id.activity_np_seekBar);
        mPlayPause = findViewById(R.id.activity_np_play_pause_btn);
        mArtistText = findViewById(R.id.activity_np_album_artist_name);
        mTrackTitle = findViewById(R.id.activity_np_song_title);
        mFavBtn = findViewById(R.id.activity_np_favourite_btn);
        mRepeatBtn = findViewById(R.id.activity_np_btn_repeat);
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
        // Calling initialize views here instead of in #onCreate()
        // guarantees that views are initialized before any callback
        // from MediaProgressUpdateHelper#Callback
        initializeViews();
        setUpOnClickListeners();
        if (null == mUpdateHelper)
            mUpdateHelper = new MediaProgressUpdateHelper(controller, this);
        mTransportControls = controller.getTransportControls();
    }

    @Override
    public void onMetadataDataChanged(MediaMetadata metadata) {
        if (metadata != null) {
            long seconds = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) / 1000;
            mSeekBar.setMax((int) seconds);
            mSeekBar.setProgress(0);
            mStartTime.setText(DateUtils.formatElapsedTime(0));
            mEndTime.setText(DateUtils.formatElapsedTime(seconds));
            mArtistText.setText(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
            mTrackTitle.setText(metadata.getText(MediaMetadata.METADATA_KEY_TITLE));

            GlideApp.with(this)
                    .load(metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
                    .transition(GenericTransitionOptions.with(R.anim.now_playing_album_card))
                    .into((ImageView) findViewById(R.id.activity_np_album_art));

            updateFavoriteBtn();
            updateRepeatBtn();
        }
    }

    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        mState = state;
        updatePlayPauseBtn();
        updateRepeatBtn();
    }

    @SuppressLint("NewApi")
    @Override
    public void updateSeekBarProgress(int progress) {
        if (mAnimateSeekBar)
            mSeekBar.setProgress(progress);
        else mSeekBar.setProgress(progress, true);
    }

    private void setUpOnClickListeners() {
        mRepeatBtn.setOnClickListener(v -> {
            boolean repeat = mTrackManager.isCurrentTrackInRepeatMode();
            mRepeatBtn.setImageResource(repeat ? R.drawable.ic_repeat : R.drawable.ic_repeat_one);
            mTrackManager.repeatCurrentTrack(!repeat);
        });

        findViewById(R.id.activity_np_skip_prev_btn)
                .setOnClickListener(v -> mTransportControls.skipToPrevious());

        mPlayPause.setOnClickListener(v -> {
            if (mState.getState() == PlaybackState.STATE_PLAYING)
                mTransportControls.pause();
            else mTransportControls.play();
        });

        findViewById(R.id.activity_np_skip_next_btn)
                .setOnClickListener(v -> mTransportControls.skipToNext());

        mFavBtn.setOnClickListener(v -> updateFavoriteItem(!mCurrentTrackFavorite));

        findViewById(R.id.activity_np_close_btn).setOnClickListener(v -> {
            finish();
            overrideExitTransition();
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStartTime.setText(DateUtils.formatElapsedTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateHelper.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Pass progress in milli seconds
                mTransportControls.seekTo(seekBar.getProgress() * 1000);
            }
        });
    }

    private void updateRepeatBtn() {
        boolean repeat = mTrackManager.isCurrentTrackInRepeatMode();
        mRepeatBtn.setImageResource(repeat ? R.drawable.ic_repeat_one : R.drawable.ic_repeat);
    }

    private void updatePlayPauseBtn() {
        if (null == mState)
            return;

        if (mState.getState() == PlaybackState.STATE_PLAYING) {
            Drawable d = getDrawable(R.drawable.play_to_pause_linear_out_slow_in);
            mPlayPause.setImageDrawable(d);
            if (d instanceof AnimatedVectorDrawable) ((AnimatedVectorDrawable) d).start();
        } else {
            Drawable d = getDrawable(R.drawable.pause_to_play);
            mPlayPause.setImageDrawable(d);
            if (d instanceof AnimatedVectorDrawable) ((AnimatedVectorDrawable) d).start();
        }
    }

    private void updateFavoriteBtn() {
        AppFileManager.isItemAFavorite(mTrackManager.getActiveQueueItem(), result ->
                mFavBtn.post(() -> mFavBtn.setSelected((mCurrentTrackFavorite = result))));
    }

    private void updateFavoriteItem(boolean makeItemFav) {
        if (makeItemFav) {
            AppFileManager.addItemToFavorites(mTrackManager.getActiveQueueItem());
            updateFavoriteBtn();
            Toast.makeText(this, getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
        } else {
            AppFileManager.deleteFavorite(mTrackManager.getActiveQueueItem());
            updateFavoriteBtn();
            Toast.makeText(this, getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
        }
    }

    private void overrideExitTransition() {
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_slide_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideExitTransition();
    }

    @Override
    protected void onDestroy() {
        mUpdateHelper.destroy();
        super.onDestroy();
    }
}
