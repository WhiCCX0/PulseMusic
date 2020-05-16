package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderHelper {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static void loadAllTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.TITLE_ASC), result -> {
            LoaderCache.setAllTracksList(result);
            callback.onComplete(result);
            final Map<String, MusicModel> modelMap = new HashMap<>();
            for (MusicModel musicModel : result)
                modelMap.put(musicModel.getSongName(), musicModel);
            LoaderCache.setModelMap(modelMap);
        });
    }

    public static void loadAlbumsList(@NonNull ContentResolver contentResolver, SortOrder.ALBUMS sortOrder, @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new AlbumsLoader(contentResolver, sortOrder), result -> {
            LoaderCache.setAlbumsList(result);
            callback.onComplete(result);
        });
    }

    public static void loadArtistsList(@NonNull ContentResolver contentResolver, SortOrder.ARTIST sortOrder, @NonNull Callback<List<ArtistModel>> callback) {
        TaskRunner.executeAsync(new ArtistsLoader(contentResolver, sortOrder), result -> {
            LoaderCache.setArtistsList(result);
            callback.onComplete(result);
        });
    }

    public static void loadSuggestionsList(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<MusicModel> list = new ArrayList<>(LoaderCache.getAllTracksList());
            Collections.shuffle(list);
            final List<MusicModel> suggestionsList = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
            LoaderCache.setSuggestions(suggestionsList);
            mHandler.post(() -> callback.onComplete(suggestionsList));
        });
    }

    public static void loadLatestTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.DATE_MODIFIED_DESC), result -> {
            List<MusicModel> finalResult = result.subList(0, (int) (result.size() * 0.2));
            LoaderCache.setLatestTracks(finalResult);
            callback.onComplete(finalResult);
        });
    }

    public static void loadTopAlbums(@NonNull ContentResolver contentResolver, @NonNull Callback<List<AlbumModel>> callback) {
        if (null == LoaderCache.getAlbumsList()) {
            loadAlbumsList(contentResolver, SortOrder.ALBUMS.TITLE_ASC, result -> {
                List<AlbumModel> list = new ArrayList<>(result);
                Collections.shuffle(list);
                final List<AlbumModel> topAlbums = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
                LoaderCache.setTopAlbums(topAlbums);
                mHandler.post(() -> callback.onComplete(topAlbums));
            });
        } else TaskRunner.executeAsync(() -> {
            List<AlbumModel> list = new ArrayList<>(LoaderCache.getAlbumsList());
            Collections.shuffle(list);
            final List<AlbumModel> topAlbums = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
            LoaderCache.setTopAlbums(topAlbums);
            mHandler.post(() -> callback.onComplete(topAlbums));
        });
    }

    public static void loadTopArtist(@NonNull ContentResolver contentResolver, @NonNull Callback<List<ArtistModel>> callback) {
        if (null == LoaderCache.getArtistsList()) {
            loadArtistsList(contentResolver, SortOrder.ARTIST.TITLE_ASC, result -> {
                List<ArtistModel> list = new ArrayList<>(result);
                Collections.shuffle(list);
                final List<ArtistModel> topAlbums = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
                LoaderCache.setTopArtists(topAlbums);
                mHandler.post(() -> callback.onComplete(topAlbums));
            });
        } else TaskRunner.executeAsync(() -> {
            List<ArtistModel> list = new ArrayList<>(LoaderCache.getArtistsList());
            Collections.shuffle(list);
            final List<ArtistModel> topAlbums = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
            LoaderCache.setTopArtists(topAlbums);
            mHandler.post(() -> callback.onComplete(topAlbums));
        });
    }
}
