package com.hardcodecoder.pulsemusic.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;

public class StorageHelper {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private StorageHelper() {
    }

    public static void getSavedHistory(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> StorageManager.getSavedHistory(context.getFilesDir().getAbsolutePath(), result -> {
            List<MusicModel> recentTracks = DataModelHelper.getModelsObjectFromTitlesList(result);
            handler.post(() -> callback.onComplete(recentTracks));
        }));
    }

    public static void addTrackToHistory(Context context, MusicModel md) {
        StorageManager.addTrackToHistory(context.getFilesDir().getAbsolutePath(), md.getSongName());
    }

    public static void getSavedFavoriteTracks(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> StorageManager.getSavedFavoriteTracks(context.getFilesDir().getAbsolutePath(), result -> {
            List<MusicModel> favoriteTracks = DataModelHelper.getModelsObjectFromTitlesList(result);
            handler.post(() -> callback.onComplete(favoriteTracks));
        }));
    }

    public static void addFavoritesList(Context context, List<MusicModel> list) {
        TaskRunner.executeAsync(() -> {
            List<String> favoritesList = DataModelHelper.getTitlesListFromModelsObject(list);
            StorageManager.addFavoritesList(context.getFilesDir().getAbsolutePath(), favoritesList);
        });
    }

    public static void getPlaylists(Context context, TaskRunner.Callback<List<String>> callback) {
        StorageManager.getPlaylists(context.getFilesDir().getAbsolutePath(), result -> handler.post(() -> callback.onComplete(result)));
    }

    public static void savePlaylist(Context context, String playlistName) {
        StorageManager.savePlaylist(context.getFilesDir().getAbsolutePath(), playlistName);
    }

    public static void getPlaylistTracks(Context context, String playlistName, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> StorageManager.getPlaylistTracks(context.getFilesDir().getAbsolutePath(), playlistName, result -> {
            List<MusicModel> playlistTracks = DataModelHelper.getModelsObjectFromTitlesList(result);
            handler.post(() -> callback.onComplete(playlistTracks));
        }));
    }

    public static void addTracksToPlaylist(Context context, String playlistName, List<MusicModel> playlistTracks, TaskRunner.Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> tracksTitleList = DataModelHelper.getTitlesListFromModelsObject(playlistTracks);
            StorageManager.addTracksToPlaylist(context.getFilesDir().getAbsolutePath(), playlistName, tracksTitleList);
            handler.post(() -> callback.onComplete(true));
        });
    }

    public static void renamePlaylist(Context context, String oldPlaylistName, String newPlaylistName) {
        StorageManager.renamePlaylist(context.getFilesDir().getAbsolutePath(), oldPlaylistName, newPlaylistName);
    }

    public static void deletePlaylist(Context context, String playlistName) {
        StorageManager.deletePlaylist(context.getFilesDir().getAbsolutePath(), playlistName);
    }

    public static void updatePlaylistTracks(Context context, String playlistName, List<MusicModel> newTracksList) {
        final String filesDir = context.getFilesDir().getAbsolutePath();
        TaskRunner.executeAsync(() -> StorageManager.updatePlaylistTracks(filesDir, playlistName, DataModelHelper.getTitlesListFromModelsObject(newTracksList)));
    }
}
