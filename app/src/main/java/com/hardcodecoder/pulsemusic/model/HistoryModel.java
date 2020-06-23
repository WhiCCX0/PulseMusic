package com.hardcodecoder.pulsemusic.model;

public class HistoryModel {

    private String title;
    private String album;
    private String artist;
    private long albumId;
    private long lastModified;
    private int playCount;

    public HistoryModel(String title, String album, String artist, long albumId, long lastModified, int playCount) {
        this.title = title;
        this.album = album;
        this.albumId = albumId;
        this.artist = artist;
        this.lastModified = lastModified;
        this.playCount = playCount;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getLastModified() {
        return lastModified;
    }

    public int getPlayCount() {
        return playCount;
    }
}
