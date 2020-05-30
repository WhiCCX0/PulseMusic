package com.hardcodecoder.pulsemusic.model;

public class TopAlbumModel {

    private String mAlbumName;
    private String mAlbumArt;
    private long mAlbumId;

    public TopAlbumModel(String mAlbumName, String mAlbumArt, long mAlbumId) {
        this.mAlbumName = mAlbumName;
        this.mAlbumArt = mAlbumArt;
        this.mAlbumId = mAlbumId;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getAlbumArt() {
        return mAlbumArt;
    }

    public long getAlbumId() {
        return mAlbumId;
    }
}
