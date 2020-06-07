package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SearchQueryLoader implements Callable<List<MusicModel>> {

    private String mSearchQuery;

    public SearchQueryLoader(String searchQuery) {
        this.mSearchQuery = searchQuery;
    }

    @Override
    public List<MusicModel> call() {
        final List<MusicModel> searchList = LoaderCache.getAllTracksList();
        List<MusicModel> searchResult = new ArrayList<>();
        if (!mSearchQuery.isEmpty() && null != searchList) {
            mSearchQuery = mSearchQuery.toLowerCase();
            for (MusicModel musicModel : searchList) {
                if (musicModel.getTrackName().toLowerCase().contains(mSearchQuery) ||
                        musicModel.getAlbum().toLowerCase().contains(mSearchQuery) ||
                        musicModel.getArtist().toLowerCase().contains(mSearchQuery)) {
                    searchResult.add(musicModel);
                }
            }
        }
        return searchResult;
    }
}
