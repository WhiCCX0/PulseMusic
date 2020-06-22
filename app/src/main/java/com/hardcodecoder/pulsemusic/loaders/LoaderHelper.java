package com.hardcodecoder.pulsemusic.loaders;

import android.content.ContentResolver;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class LoaderHelper {

    public static void loadAllTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.TITLE_ASC), result -> {
            LoaderCache.setAllTracksList(result);
            result.clear();
            callback.onComplete(LoaderCache.getAllTracksList());
        });
    }

    public static void loadAlbumsList(ContentResolver contentResolver, SortOrder.ALBUMS sortOrder, @NonNull Callback<List<AlbumModel>> callback) {
        TaskRunner.executeAsync(new AlbumsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadArtistsList(@NonNull ContentResolver contentResolver, SortOrder.ARTIST sortOrder, @NonNull Callback<List<ArtistModel>> callback) {
        TaskRunner.executeAsync(new ArtistsLoader(contentResolver, sortOrder), callback);
    }

    public static void loadSuggestionsList(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<MusicModel> list = new ArrayList<>(LoaderCache.getAllTracksList());
            if (list.size() > 0) {
                Collections.shuffle(list);
                final List<MusicModel> suggestionsList = list.subList(0, (int) (list.size() * 0.2));  //only top 20%
                LoaderCache.setSuggestions(suggestionsList);
                suggestionsList.clear();
                list.clear();
                callback.onComplete(LoaderCache.getSuggestions());
            }
        });
    }

    public static void loadRecentTracks(@NonNull Context context, @NonNull Callback<List<MusicModel>> callback) {
        AppFileManager.getHistory(context, true, result -> {
            if (null != result && result.size() > 0) {
                Map<String, MusicModel> map = new Hashtable<>();
                for (MusicModel md : LoaderCache.getAllTracksList())
                    map.put(md.getTrackName(), md);
                List<MusicModel> recentTracks = new ArrayList<>(result.size());
                for (HistoryModel hm : result)
                    recentTracks.add(map.get(hm.getTitle()));
                callback.onComplete(recentTracks);
            } else callback.onComplete(null);
        });
    }

    public static void loadLatestTracks(@NonNull ContentResolver contentResolver, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.DATE_MODIFIED_DESC), result -> {
            if (null != result && result.size() > 0) {
                List<MusicModel> finalResult = result.subList(0, (int) (result.size() * 0.2));
                LoaderCache.setLatestTracks(finalResult);
                finalResult.clear();
                result.clear();
                callback.onComplete(LoaderCache.getLatestTracks());
            }
        });
    }

    public static void loadTopAlbums(@NonNull Context context, @NonNull Callback<List<TopAlbumModel>> callback) {
        AppFileManager.getHistory(context, false, history -> {
            if (null != history && history.size() > 0)
                TaskRunner.executeAsync(new TopAlbumsLoader(history), callback);
            else callback.onComplete(null);
        });
    }

    public static void loadTopArtist(@NonNull Context context, @NonNull Callback<List<TopArtistModel>> callback) {
        AppFileManager.getHistory(context, false, history -> {
            if (null != history && history.size() > 0)
                TaskRunner.executeAsync(new TopArtistsLoader(history), callback);
            else callback.onComplete(null);
        });
    }
}
