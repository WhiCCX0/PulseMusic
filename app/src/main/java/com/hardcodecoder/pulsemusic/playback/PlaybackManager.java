package com.hardcodecoder.pulsemusic.playback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;

import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.io.InputStream;

public class PlaybackManager implements Playback.Callback {

    public static final short ACTION_PLAY_NEXT = 1;
    public static final short ACTION_PLAY_PREV = -1;
    private final PlaybackState.Builder mStateBuilder = new PlaybackState.Builder();
    private final MediaMetadata.Builder mMetadataBuilder = new MediaMetadata.Builder();
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private TrackManager mTrackManager;
    private Context mContext;
    private boolean mManualPause;
    private final MediaSession.Callback mMediaSessionCallback = new MediaSession.Callback() {

        @Override
        public void onPlay() {
            handlePlayRequest(false);
            mManualPause = false;
        }

        @Override
        public void onPause() {
            handlePauseRequest();
            mManualPause = true;
        }

        @Override
        public void onSkipToNext() {
            handleSkipRequest(ACTION_PLAY_NEXT);
        }

        @Override
        public void onSkipToPrevious() {
            handleSkipRequest(ACTION_PLAY_PREV);
        }

        @Override
        public void onStop() {
            handleStopRequest();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.onSeekTo((int) pos);
        }
    };

    public PlaybackManager(Context context, Playback playback, PlaybackServiceCallback serviceCallback) {
        this.mContext = context;
        this.mPlayback = playback;
        this.mTrackManager = TrackManager.getInstance();
        this.mServiceCallback = serviceCallback;
        mPlayback.setCallback(this);
    }

    public MediaSession.Callback getSessionCallbacks() {
        return mMediaSessionCallback;
    }

    private void handlePlayRequest(boolean repeatMode) {
        boolean hasMediaChanged = false;
        if (!repeatMode) {
            MusicModel md = mTrackManager.getActiveQueueItem();
            if (mPlayback.getActiveMediaId() != md.getId())
                hasMediaChanged = true;
            mServiceCallback.onPlaybackStart();
            updateMetaData(md, hasMediaChanged);
        }
        mPlayback.onPlay(hasMediaChanged, repeatMode);
    }

    private void handlePauseRequest() {
        mServiceCallback.onPlaybackStopped();
        mPlayback.onPause();
    }

    private void handleStopRequest() {
        mServiceCallback.onPlaybackStopped();
        mPlayback.onStop(true);
    }

    private void handleSkipRequest(short di) {
        if (mTrackManager.canSkipTrack(di))
            handlePlayRequest(mTrackManager.isCurrentTrackInRepeatMode());
        else
            handlePauseRequest();
    }

    private void updatePlaybackState(int currentState) {
        mStateBuilder.setState(currentState, mPlayback.getCurrentStreamingPosition(), currentState == PlaybackState.STATE_PLAYING ? 1 : 0);
        mStateBuilder.setActions(getActions(currentState));
        mServiceCallback.onPlaybackStateChanged(mStateBuilder.build());

        if (currentState == PlaybackState.STATE_PLAYING) {
            mServiceCallback.onStartNotification();
        } else if (currentState == PlaybackState.STATE_STOPPED) {
            mServiceCallback.onStopNotification();
        }
    }

    private void updateMetaData(MusicModel md, boolean b) {
        if (b) {
            mMetadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, md.getTrackDuration());
            mMetadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, md.getTrackName());
            mMetadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, md.getArtist());
            mMetadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM, md.getAlbum());
            mMetadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, loadAlbumArt(md.getAlbumArtUrl(), md.getAlbumId()));
            mServiceCallback.onMetaDataChanged(mMetadataBuilder.build());
        }
    }

    private Bitmap loadAlbumArt(String path, long albumId) {
        // We know that manually selected tracks have negative album id
        if (albumId < 0)
            return MediaArtHelper.loadDynamicAlbumArtBitmap(mContext, albumId);
        try {
            Uri uri = Uri.parse(path);
            InputStream is = mContext.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            return MediaArtHelper.loadDynamicAlbumArtBitmap(mContext, albumId);
        }
    }

    private long getActions(int state) {
        long actions;
        if (state == PlaybackState.STATE_PLAYING) {
            actions = PlaybackState.ACTION_PAUSE;
        } else {
            actions = PlaybackState.ACTION_PLAY;
        }
        return PlaybackState.ACTION_SKIP_TO_PREVIOUS | PlaybackState.ACTION_SKIP_TO_NEXT | actions;
    }

    @Override
    public void onFocusChanged(boolean resumePlayback) {
        if (mManualPause)
            return;
        if (resumePlayback)
            handlePlayRequest(false);
        else
            handlePauseRequest();
    }

    @Override
    public void onPlaybackCompletion() {
        handleSkipRequest(ACTION_PLAY_NEXT);
    }

    @Override
    public void onPlaybackStateChanged(int state) {
        updatePlaybackState(state);
    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onPlaybackStopped();

        void onStartNotification();

        void onStopNotification();

        void onPlaybackStateChanged(PlaybackState newState);

        void onMetaDataChanged(MediaMetadata newMetaData);
    }
}
