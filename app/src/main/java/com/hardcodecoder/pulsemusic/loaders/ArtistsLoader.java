package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.ArtistModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ArtistsLoader implements Callable<List<ArtistModel>> {

    private ContentResolver mContentResolver;
    private String mSortOrder;

    ArtistsLoader(ContentResolver mContentResolver, SortOrder.ARTIST sortOrder) {
        this.mContentResolver = mContentResolver;
        switch (sortOrder) {
            case TITLE_ASC:
                mSortOrder = MediaStore.Audio.Artists.ARTIST + " COLLATE NOCASE ASC";
                break;
            case TITLE_DESC:
                mSortOrder = MediaStore.Audio.Artists.ARTIST + " COLLATE NOCASE DESC";
                break;
            case NUM_OF_TRACKS_ASC:
                mSortOrder = MediaStore.Audio.Artists.NUMBER_OF_TRACKS + " ASC";
                break;
            case NUM_OF_TRACKS_DESC:
                mSortOrder = MediaStore.Audio.Artists.NUMBER_OF_TRACKS + " DESC";
                break;
            default:
                mSortOrder = null;
        }
    }

    @Override
    public List<ArtistModel> call() {
        List<ArtistModel> artistList = new ArrayList<>();
        String[] col = {MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                col,
                null,
                null,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
            int albumCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
            int trackCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

            do {
                int id = cursor.getInt(idColumnIndex);
                String artist = cursor.getString(artistColumnIndex);
                int num_albums = cursor.getInt(albumCountColumnIndex);
                int num_tracks = cursor.getInt(trackCountColumnIndex);

                artistList.add(new ArtistModel(id, num_albums, num_tracks, artist));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return artistList;
    }
}
