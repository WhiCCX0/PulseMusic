package com.hardcodecoder.pulsemusic.model;

public class AlbumModel {

    private int mId;
    private int mSongsCount;
    private long mAlbumId;
    private String mAlbumName;
    private String mAlbumArt;

    public AlbumModel(int mId, int mSongsCount,  long albumId, String mAlbumName, String mAlbumArt) {
        this.mId = mId;
        this.mSongsCount = mSongsCount;
        this.mAlbumId = albumId;
        this.mAlbumName = mAlbumName;
        this.mAlbumArt = mAlbumArt;
    }

    public int getId() {
        return mId;
    }

    public long getAlbumId() { return mAlbumId; }

    public int getSongsCount() {
        return mSongsCount;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getAlbumArt() {
        return mAlbumArt;
    }
}
