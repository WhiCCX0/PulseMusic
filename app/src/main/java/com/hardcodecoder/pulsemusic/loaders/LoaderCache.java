package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderCache {

    private static List<MusicModel> mAllTracksList;
    private static List<MusicModel> mSuggestions;
    private static List<MusicModel> mLatestTracks;
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
