package com.hardcodecoder.pulsemusic.model;

public class ArtistModel {

    private int mId;
    private int mNumOfAlbums;
    private int mNumOfTracks;
    private String mArtistName;

    public ArtistModel(int mId, int mNumOfAlbums, int mNumOfTracks, String mArtistName) {
        this.mId = mId;
        this.mNumOfAlbums = mNumOfAlbums;
        this.mNumOfTracks = mNumOfTracks;
        this.mArtistName = mArtistName;
    }

    public int getId() {
        return mId;
    }

    public int getNumOfAlbums() {
        return mNumOfAlbums;
    }

    public int getNumOfTracks() {
        return mNumOfTracks;
    }

    public String getArtistName() {
        return mArtistName;
    }
}
