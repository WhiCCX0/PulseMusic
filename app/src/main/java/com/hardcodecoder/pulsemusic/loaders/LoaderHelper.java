package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.storage.StorageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderHelper {

    public static void loadAllTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.TITLE_ASC), result -> {
            LoaderCache.setAllTracksList(result);
            callback.onComplete(LoaderCache.getAllTracksList());
            TaskRunner.executeAsync(() -> {
                Map<String, MusicModel> modelMap = new HashMap<>();
                for (MusicModel musicModel : result)
                    modelMap.put(musicModel.getTrackName(), musicModel);
                LoaderCache.setModelMap(modelMap);
                modelMap.clear();
                result.clear();
            });
        });
    }

    public static void loadAlbumsList(SortOrder.ALBUMS sortOrder, @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new AlbumsLoader(sortOrder), callback);
    }

    public static void loadArtistsList(@NonNull ContentResolver contentResolver, SortOrder.ARTIST sortOrder, @NonNull Callback<List<ArtistModel>> callback) {
        TaskRunner.executeAsync(new ArtistsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadSuggestionsList(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<MusicModel> list = new ArrayList<>(LoaderCache.getAllTracksList());
            Collections.shuffle(list);
            final List<MusicModel> suggestionsList = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
            LoaderCache.setSuggestions(suggestionsList);
            suggestionsList.clear();
            list.clear();
            callback.onComplete(LoaderCache.getSuggestions());
        });
    }

    public static void loadLatestTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.DATE_MODIFIED_DESC), result -> {
            List<MusicModel> finalResult = result.subList(0, (int) (result.size() * 0.2));
            LoaderCache.setLatestTracks(finalResult);
            finalResult.clear();
            result.clear();
            callback.onComplete(LoaderCache.getLatestTracks());
        });
    }

    public static void loadTopAlbums(@NonNull Context context, @NonNull Callback<List<TopAlbumModel>> callback) {
        StorageHelper.getSavedHistory(context, result -> {
            if (null != result && result.size() > 0)
                TaskRunner.executeAsync(new TopAlbumsLoader(result), callback);
            else callback.onComplete(null);
        });
    }

    public static void loadTopArtist(@NonNull Context context, @NonNull Callback<List<TopArtistModel>> callback) {
        StorageHelper.getSavedHistory(context, result -> {
            if (null != result && result.size() > 0)
                TaskRunner.executeAsync(new TopArtistsLoader(context.getContentResolver(), result), callback);
        });
    }
}
