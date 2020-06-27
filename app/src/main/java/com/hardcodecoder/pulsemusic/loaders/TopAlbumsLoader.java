package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentUris;
import android.net.Uri;

import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TopAlbumsLoader implements Callable<List<TopAlbumModel>> {

    private List<HistoryModel> mHistoryList;

    TopAlbumsLoader(List<HistoryModel> recentTracks) {
        mHistoryList = new ArrayList<>(recentTracks);
    }

    @Override
    public List<TopAlbumModel> call() {
        Map<String, Integer> frequency = new HashMap<>();
        Map<String, HistoryModel> modelMap = new HashMap<>();

        for (HistoryModel hm : mHistoryList) {
            Integer count = frequency.get(hm.getAlbum());
            frequency.put(hm.getAlbum(), (null == count) ? hm.getPlayCount() : count + hm.getPlayCount());
            modelMap.put(hm.getAlbum(), hm);
        }

        List<TopAlbumModel> topAlbums = new ArrayList<>();

        final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            HistoryModel hm = modelMap.get(entry.getKey());
            if (null != hm) {
                String albumArt = ContentUris.withAppendedId(sArtworkUri, hm.getAlbumId()).toString();
                topAlbums.add(new TopAlbumModel(hm.getAlbum(), albumArt, hm.getAlbumId(), entry.getValue()));
            }
        }

        Collections.sort(topAlbums, (o1, o2) -> {
            int count = o2.getPlayCount() - o1.getPlayCount();
            if (count == 0) {
                HistoryModel h1 = modelMap.get(o1.getAlbumName());
                HistoryModel h2 = modelMap.get(o2.getAlbumName());
                if (h1 != null && h2 != null) {
                    long d = h2.getLastModified() - h1.getLastModified();
                    return d > 0 ? 1 : (d == 0 ? 0 : -1);
                }
                return 0;
            }
            return count;
        });
        return topAlbums;
    }
}
