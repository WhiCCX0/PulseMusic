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
        if (null != mAllTracksList) {
            // Let gc do its work
            mAllTracksList.clear();
            mAllTracksList = null;
        }
        mAllTracksList = new ArrayList<>(allTracksList);
    }

    public static List<MusicModel> getSuggestions() {
        return mSuggestions;
    }

    static void setSuggestions(List<MusicModel> suggestions) {
        if (null != mSuggestions) {
            // Let gc do its work
            mSuggestions.clear();
            mSuggestions = null;
        }
        mSuggestions = new ArrayList<>(suggestions);
    }

    public static List<MusicModel> getLatestTracks() {
        return mLatestTracks;
    }

    static void setLatestTracks(List<MusicModel> latestTracks) {
        if (null != mLatestTracks) {
            // Let gc do its work
            mLatestTracks.clear();
            mLatestTracks = null;
        }
        mLatestTracks = new ArrayList<>(latestTracks);
    }
}
