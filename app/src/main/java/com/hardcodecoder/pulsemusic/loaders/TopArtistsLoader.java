package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TopArtistsLoader implements Callable<List<TopArtistModel>> {

    private ContentResolver mContentResolver;
    private List<MusicModel> mRecentTracks;

    TopArtistsLoader(ContentResolver contentResolver, List<MusicModel> recentTracks) {
        mContentResolver = contentResolver;
        mRecentTracks = new ArrayList<>(recentTracks);
    }

    @Override
    public List<TopArtistModel> call() {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (MusicModel md : mRecentTracks) {
            String artist = md.getArtist();
            Integer count = frequencyMap.get(artist);
            frequencyMap.put(artist, (count == null) ? 1 : count + 1);
        }

        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());
        Collections.sort(entryList, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        if (entryList.size() > 20)
            entryList = entryList.subList(0, 20);

        List<String> namesList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, Integer> entry : entryList) {
            namesList.add(entry.getKey());
            stringBuilder.append("'").append(entry.getKey()).append("', ");
        }
        stringBuilder.append("'").append("'");

        Map<String, TopArtistModel> artistModelMap = loadSelectedArtistsDetails(stringBuilder.toString());
        List<TopArtistModel> topArtistList = new ArrayList<>();
        for (String artist : namesList) {
            TopArtistModel artistModel = artistModelMap.get(artist);
            if (null != artistModel)
                topArtistList.add(artistModel);
        }
        artistModelMap.clear();
        Collections.reverse(topArtistList);
        return topArtistList;
    }

    private Map<String, TopArtistModel> loadSelectedArtistsDetails(final String names) {
        Map<String, TopArtistModel> artistMap = new HashMap<>();
        String[] col = {MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS};
        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                col, MediaStore.Audio.Artists.ARTIST + " IN  (" + names + ")", null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
            int trackCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

            do {
                String artist = cursor.getString(artistColumnIndex);
                int num_tracks = cursor.getInt(trackCountColumnIndex);

                artistMap.put(artist, new TopArtistModel(artist, num_tracks));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return artistMap;
    }
}
