package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class MusicModel implements Serializable {

    private String mTrackName, mTrackPath, mAlbum, mArtist, mAlbumArtUrl;
    private long mAlbumId;
    private int mId, mTrackDuration;

    public MusicModel(int mId,
                      @NonNull String mTrackName,
                      @NonNull String mTrackPath,
                      @NonNull String mAlbum,
                      @NonNull String mArtist,
                      @Nullable String mAlbumArtUrl,
                      long mAlbumId,
                      int mTrackDuration) {
        this.mId = mId;
        this.mTrackName = mTrackName;
        this.mTrackPath = mTrackPath;
        this.mAlbum = mAlbum;
        this.mArtist = mArtist;
        this.mAlbumArtUrl = mAlbumArtUrl;
        this.mAlbumId = mAlbumId;
        this.mTrackDuration = mTrackDuration;
    }

    public int getId() {
        return mId;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public String getTrackPath() {
        return mTrackPath;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbumArtUrl() {
        return mAlbumArtUrl;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public int getTrackDuration() {
        return mTrackDuration;
    }
}
