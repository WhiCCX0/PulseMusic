package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;
import java.util.Map;

public class LoaderCache {

    private static List<MusicModel> mAllTracksList;
    private static List<AlbumModel> mAlbumsList;
    private static List<ArtistModel> mArtistsList;
    private static List<MusicModel> mSuggestions;
    private static List<MusicModel> mLatestTracks;
    private static List<AlbumModel> mTopAlbums;
    private static List<ArtistModel> mTopArtists;
    private static Map<String, MusicModel> mModelMap;

    public static List<MusicModel> getAllTracksList() {
        return mAllTracksList;
    }

    static void setAllTracksList(List<MusicModel> allTracksList) {
        mAllTracksList = allTracksList;
    }

    public static List<AlbumModel> getAlbumsList() {
        return mAlbumsList;
    }

    static void setAlbumsList(List<AlbumModel> albumsList) {
        mAlbumsList = albumsList;
    }

    public static List<ArtistModel> getArtistsList() {
        return mArtistsList;
    }

    static void setArtistsList(List<ArtistModel> artistsList) {
        mArtistsList = artistsList;
    }

    public static List<MusicModel> getSuggestions() {
        return mSuggestions;
    }

    static void setSuggestions(List<MusicModel> suggestions) {
        mSuggestions = suggestions;
    }

    public static List<MusicModel> getLatestTracks() {
        return mLatestTracks;
    }

    static void setLatestTracks(List<MusicModel> latestTracks) {
        mLatestTracks = latestTracks;
    }

    public static List<AlbumModel> getTopAlbums() {
        return mTopAlbums;
    }

    static void setTopAlbums(List<AlbumModel> mTopAlbums) {
        LoaderCache.mTopAlbums = mTopAlbums;
    }

    public static List<ArtistModel> getTopArtists() {
        return mTopArtists;
    }

    static void setTopArtists(List<ArtistModel> mTopArtists) {
        LoaderCache.mTopArtists = mTopArtists;
    }

    public static Map<String, MusicModel> getModelMap() {
        return mModelMap;
    }

    static void setModelMap(Map<String, MusicModel> modelMap) {
        mModelMap = modelMap;
    }
}
