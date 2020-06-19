package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class LoaderCache {

    private static List<MusicModel> mAllTracksList;
    private static List<MusicModel> mSuggestions;
    private static List<MusicModel> mLatestTracks;

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
}
