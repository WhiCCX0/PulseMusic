package com.hardcodecoder.pulsemusic.model;

public class TopArtistModel {

    private String mArtistName;
    private int mNumOfTracks;

    public TopArtistModel(String mArtistName, int mNumOfTracks) {
        this.mArtistName = mArtistName;
        this.mNumOfTracks = mNumOfTracks;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public int getNumOfTracks() {
        return mNumOfTracks;
    }
}
