package com.hardcodecoder.pulsemusic.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorageHelper {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final StorageManager mStorageManager = new StorageManager();
    private static HashSet<String> favoritesSet = new HashSet<>();
    private static Set<String> historySet = new HashSet<>();

    private StorageHelper() {
    }

    public static void getSavedHistory(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> historyList = mStorageManager.getSavedHistory(context.getFilesDir().getAbsolutePath());
            if (null != historyList && historyList.size() > 0) {
                historySet.clear();
                historySet.addAll(historyList);
                List<MusicModel> recentTracks = DataModelHelper.getModelsObjectFromTitlesList(historyList);
                handler.post(() -> callback.onComplete(recentTracks));
            } else handler.post(() -> callback.onComplete(null));
        });
    }

    public static void addTrackToHistory(Context context, MusicModel md) {
        if (historySet.add(md.getTrackName())) // Returns true if the song title is not present in the set
            TaskRunner.executeAsync(() -> mStorageManager.addTrackToHistory(context.getFilesDir().getAbsolutePath(), md.getTrackName(), 1));
    }

    public static void getSavedFavoriteTracks(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> favoritesList = mStorageManager.getSavedFavoriteTracks(context.getFilesDir().getAbsolutePath());
            if (null != favoritesList && favoritesList.size() > 0) {
                favoritesSet.addAll(favoritesList);
                List<MusicModel> favoriteTracks = DataModelHelper.getModelsObjectFromTitlesList(favoritesList);
                handler.post(() -> callback.onComplete(favoriteTracks));
            } else handler.post(() -> callback.onComplete(null));
        });
    }

    public static void addTrackToFavorites(Context context, MusicModel md) {
        if (favoritesSet.add(md.getTrackName()))
            TaskRunner.executeAsync(() -> mStorageManager.addTrackToFavorites(context.getFilesDir().getAbsolutePath(), md.getTrackName()));
    }

    public static void removeTrackFromFavorites(Context context, MusicModel md) {
        if (favoritesSet.remove(md.getTrackName())) {
            // Item was previously added and has been removed successfully.
            // Remove from database as well
            TaskRunner.executeAsync(() -> mStorageManager.deleteTrackFromFavoritesList(context.getFilesDir().getAbsolutePath(), md.getTrackName()));
        }
    }

    public static boolean isTrackAlreadyInFavorites(MusicModel md) {
        return favoritesSet.contains(md.getTrackName());
    }

    public static void getPlaylists(Context context, TaskRunner.Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> playlistList = mStorageManager.getPlaylists(context.getFilesDir().getAbsolutePath());
            handler.post(() -> callback.onComplete(playlistList));
        });
    }

    public static void savePlaylist(Context context, String playlistName) {
        TaskRunner.executeAsync(() -> mStorageManager.savePlaylist(context.getFilesDir().getAbsolutePath(), playlistName));
    }

    public static void getPlaylistTracks(Context context, String playlistName, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> playlistTracksList = mStorageManager.getPlaylistTracks(context.getFilesDir().getAbsolutePath(), playlistName);
            List<MusicModel> playlistTracks = DataModelHelper.getModelsObjectFromTitlesList(playlistTracksList);
            handler.post(() -> callback.onComplete(playlistTracks));
        });
    }

    public static void addTracksToPlaylist(Context context, String playlistName, List<MusicModel> playlistTracks, TaskRunner.Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> tracksTitleList = DataModelHelper.getTitlesListFromModelsObject(playlistTracks);
            mStorageManager.addTracksToPlaylist(context.getFilesDir().getAbsolutePath(), playlistName, tracksTitleList);
            handler.post(() -> callback.onComplete(true));
        });
    }

    public static void renamePlaylist(Context context, String oldPlaylistName, String newPlaylistName) {
        TaskRunner.executeAsync(() -> mStorageManager.renamePlaylist(context.getFilesDir().getAbsolutePath(), oldPlaylistName, newPlaylistName));
    }

    public static void deletePlaylist(Context context, String playlistName) {
        TaskRunner.executeAsync(() -> mStorageManager.deletePlaylist(context.getFilesDir().getAbsolutePath(), playlistName));
    }

    public static void updatePlaylistTracks(Context context, String playlistName, List<MusicModel> newTracksList) {
        TaskRunner.executeAsync(() -> {
            final String filesDir = context.getFilesDir().getAbsolutePath();
            final List<String> playlistTitlesLIst = DataModelHelper.getTitlesListFromModelsObject(newTracksList);
            mStorageManager.updatePlaylistTracks(filesDir, playlistName, playlistTitlesLIst);
        });
    }
}
