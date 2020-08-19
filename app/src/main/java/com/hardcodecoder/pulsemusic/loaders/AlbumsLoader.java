package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.hardcodecoder.pulsemusic.model.AlbumModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AlbumsLoader implements Callable<List<AlbumModel>> {

    private ContentResolver mContentResolver;
    private String mSortOrder;

    AlbumsLoader(ContentResolver mContentResolver, SortOrder.ALBUMS sortOrder) {
        this.mContentResolver = mContentResolver;
        switch (sortOrder) {
            case TITLE_ASC:
                mSortOrder = MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE ASC";
                break;
            case TITLE_DESC:
                mSortOrder = MediaStore.Audio.Albums.ALBUM + " COLLATE NOCASE DESC";
                break;
            case ALBUM_DATE_FIRST_YEAR_ASC:
                mSortOrder = MediaStore.Audio.Albums.FIRST_YEAR + " ASC";
                break;
            case ALBUM_DATE_FIRST_YEAR_DESC:
                mSortOrder = MediaStore.Audio.Albums.FIRST_YEAR + " DESC";
                break;
            case ALBUM_DATE_LAST_YEAR_ASC:
                mSortOrder = MediaStore.Audio.Albums.LAST_YEAR + " ASC";
                break;
            case ALBUM_DATE_LAST_YEAR_DESC:
                mSortOrder = MediaStore.Audio.Albums.LAST_YEAR + " DESC";
                break;
            default:
                mSortOrder = null;
        }
    }

    @Override
    public List<AlbumModel> call() {
        List<AlbumModel> albumsList = new ArrayList<>();
        String[] col = {BaseColumns._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};
        final Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                col,
                null,
                null,
                mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
            int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID);
            int songCountColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            do {
                String album = cursor.getString(albumColumnIndex);
                if (null != album) {
                    int id = cursor.getInt(idColumnIndex);
                    int num = cursor.getInt(songCountColumnIndex);
                    long albumId = cursor.getLong(albumIdColumnIndex);
                    String albumArt = ContentUris.withAppendedId(sArtworkUri, albumId).toString();
                    albumsList.add(new AlbumModel(id, num, albumId, album, albumArt));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumsList;
    }
}
