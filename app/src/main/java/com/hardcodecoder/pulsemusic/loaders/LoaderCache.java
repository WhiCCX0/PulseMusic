package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderCache {

    private static List<MusicModel> mAllTracksList;
    private static List<AlbumModel> mAlbumsList;
    private static List<ArtistModel> mArtistsList;
    private static List<MusicModel> mSuggestions;
    private static List<MusicModel> mLatestTracks;
    private static List<TopAlbumModel> mTopAlbums;
    private static List<ArtistModel> mTopArtists;
    private static Map<String, MusicModel> mModelMap;

    public static List<MusicModel> getAllTracksList() {
        return mAllTracksList;
    }

    static void setAllTracksList(List<MusicModel> allTracksList) {
        if (null == mAllTracksList)
            mAllTracksList = new ArrayList<>(allTracksList);
        else {
            mAllTracksList.clear();
            mAllTracksList.addAll(allTracksList);
        }
    }

    public static List<AlbumModel> getAlbumsList() {
        return mAlbumsList;
    }

    static void setAlbumsList(List<AlbumModel> albumsList) {
        if (null == mAlbumsList)
            mAlbumsList = new ArrayList<>(albumsList);
        else {
            mAlbumsList.clear();
            mAlbumsList.addAll(albumsList);
        }
    }

    public static List<ArtistModel> getArtistsList() {
        return mArtistsList;
    }

    static void setArtistsList(List<ArtistModel> artistsList) {
        if (null == mArtistsList)
            mArtistsList = new ArrayList<>(artistsList);
        else {
            mArtistsList.clear();
            mArtistsList.addAll(artistsList);
        }
    }

    public static List<MusicModel> getSuggestions() {
        return mSuggestions;
    }

    static void setSuggestions(List<MusicModel> suggestions) {
        if (null == mSuggestions)
            mSuggestions = new ArrayList<>(suggestions);
        else {
            mSuggestions.clear();
            mSuggestions.addAll(suggestions);
        }
    }

    public static List<MusicModel> getLatestTracks() {
        return mLatestTracks;
    }

    static void setLatestTracks(List<MusicModel> latestTracks) {
        if (null == mLatestTracks)
            mLatestTracks = new ArrayList<>(latestTracks);
        else {
            mLatestTracks.clear();
            mLatestTracks.addAll(latestTracks);
        }
    }

    public static List<TopAlbumModel> getTopAlbums() {
        return mTopAlbums;
    }

    static void setTopAlbums(List<TopAlbumModel> topAlbums) {
        if (null == mTopAlbums)
            mTopAlbums = new ArrayList<>(topAlbums);
        else {
            mTopAlbums.clear();
            mTopAlbums.addAll(topAlbums);
        }
    }

    public static List<ArtistModel> getTopArtists() {
        return mTopArtists;
    }

    static void setTopArtists(List<ArtistModel> topArtists) {
        if (null == mTopArtists)
            mTopArtists = new ArrayList<>(topArtists);
        else {
            mTopArtists.clear();
            mTopArtists.addAll(topArtists);
        }
    }

    public static Map<String, MusicModel> getModelMap() {
        return mModelMap;
    }

    static void setModelMap(Map<String, MusicModel> modelMap) {
        if (null == mModelMap)
            mModelMap = new HashMap<>(modelMap);
        else {
            mModelMap.clear();
            mModelMap.putAll(modelMap);
        }
    }
}
