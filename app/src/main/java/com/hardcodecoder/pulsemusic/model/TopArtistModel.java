package com.hardcodecoder.pulsemusic.model;

public class TopArtistModel {

    private String artistName;
    private int numOfPlays;

    public TopArtistModel(String artistName, int numOfPlays) {
        this.artistName = artistName;
        this.numOfPlays = numOfPlays;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getNumOfPlays() {
        return numOfPlays;
    }
}
