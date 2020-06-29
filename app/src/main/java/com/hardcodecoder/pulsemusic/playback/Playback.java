package com.hardcodecoder.pulsemusic.playback;

public interface Playback {

    void onPlay(boolean mediaHasChanged, boolean repeatMode);

    void onPause();

    void onSeekTo(int position);

    void onStop(boolean abandonAudioFocus);

    int getActiveMediaId();

    boolean isPlaying();

    long getCurrentStreamingPosition();

    int getPlaybackState();

    void setCallback(Callback callback);

    interface Callback {
        void onPlaybackCompletion();

        void onPlaybackStateChanged(int state);

        void onFocusChanged(boolean resumePlayback);
    }
}
