package com.hardcodecoder.pulsemusic.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorageHelper {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static HashSet<String> favoritesSet = new HashSet<>();
    private static Set<String> historySet = new HashSet<>();

    private StorageHelper() {
    }

    public static void getSavedHistory(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> StorageManager.getSavedHistory(context.getFilesDir().getAbsolutePath(), result -> {
            historySet.clear();
            historySet.addAll(result);
            List<MusicModel> recentTracks = DataModelHelper.getModelsObjectFromTitlesList(result);
            Collections.reverse(recentTracks);
            handler.post(() -> callback.onComplete(recentTracks));
        }));
    }

    public static void addTrackToHistory(Context context, MusicModel md) {
        if (historySet.add(md.getSongName())) // Returns true if the song title is not present in the set
            StorageManager.addTrackToHistory(context.getFilesDir().getAbsolutePath(), md.getSongName());
    }

    public static void getSavedFavoriteTracks(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> StorageManager.getSavedFavoriteTracks(context.getFilesDir().getAbsolutePath(), result -> {
            favoritesSet.addAll(result);
            List<MusicModel> favoriteTracks = DataModelHelper.getModelsObjectFromTitlesList(result);
            handler.post(() -> callback.onComplete(favoriteTracks));
        }));
    }

    public static void addTrackToFavorites(Context context, MusicModel md) {
        if (favoritesSet.add(md.getSongName()))
            StorageManager.addTrackToFavorites(context.getFilesDir().getAbsolutePath(), md.getSongName());
    }

    public static void removeTrackFromFavorites(Context context, MusicModel md) {
        if (favoritesSet.remove(md.getSongName())) {
            // Item was previously added and has been removed successfully.
            // Remove from database as well
            StorageManager.deleteTrackFromFavoritesList(context.getFilesDir().getAbsolutePath(), md.getSongName());
        }
    }

    public static boolean isTrackAlreadyInFavorites(MusicModel md) {
        return favoritesSet.contains(md.getSongName());
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
