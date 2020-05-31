package com.hardcodecoder.pulsemusic.loaders;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TopAlbumsLoader implements Callable<List<TopAlbumModel>> {

    private List<MusicModel> mRecentTracks;

    TopAlbumsLoader(List<MusicModel> recentTracks) {
        mRecentTracks = new ArrayList<>(recentTracks);
    }

    @Override
    public List<TopAlbumModel> call() {
        List<TopAlbumModel> topAlbums = new ArrayList<>();
        Map<String, Integer> frequencyMap = new HashMap<>();
        Map<String, MusicModel> modelMap = new HashMap<>();

        for (MusicModel md : mRecentTracks) {
            String albumName = md.getAlbum();
            Integer count = frequencyMap.get(albumName);
            frequencyMap.put(albumName, (count == null) ? 1 : count + 1);
            modelMap.put(albumName, md);
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());
        Collections.sort(entryList, Collections.reverseOrder((o1, o2) -> o1.getValue().compareTo(o2.getValue())));

        if (entryList.size() > 20)
            entryList = entryList.subList(0, 20);

        for (Map.Entry<String, Integer> entry : entryList) {
            MusicModel md = modelMap.get(entry.getKey());
            if (null != md)
                topAlbums.add(new TopAlbumModel(md.getAlbum(), md.getAlbumArtUrl(), md.getAlbumId()));
        }
        return topAlbums;
    }
}
