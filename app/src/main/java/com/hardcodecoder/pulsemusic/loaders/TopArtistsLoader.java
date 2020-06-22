package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TopArtistsLoader implements Callable<List<TopArtistModel>> {

    private List<HistoryModel> mHistoryList;

    TopArtistsLoader(List<HistoryModel> recentTracks) {
        mHistoryList = new ArrayList<>(recentTracks);
    }

    @Override
    public List<TopArtistModel> call() {
        Map<String, Integer> frequency = new HashMap<>();
        Map<String, HistoryModel> modelMap = new HashMap<>();

        for (HistoryModel hm : mHistoryList) {
            Integer count = frequency.get(hm.getAlbum());
            frequency.put(hm.getArtist(), (null == count) ? hm.getPlayCount() : count + hm.getPlayCount());
            modelMap.put(hm.getArtist(), hm);
        }

        List<TopArtistModel> topArtistList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : frequency.entrySet())
            topArtistList.add(new TopArtistModel(entry.getKey(), entry.getValue()));

        Collections.sort(topArtistList, (o1, o2) -> {
            int count = o2.getNumOfPlays() - o1.getNumOfPlays();
            if (count == 0) {
                HistoryModel h1 = modelMap.get(o1.getArtistName());
                HistoryModel h2 = modelMap.get(o2.getArtistName());
                if (h1 != null && h2 != null) {
                    long d = h2.getLastModified() - h1.getLastModified();
                    return d > 0 ? 1 : (d == 0 ? 0 : -1);
                }
                return 0;
            }
            return count;
        });
        return topArtistList;
    }
}
