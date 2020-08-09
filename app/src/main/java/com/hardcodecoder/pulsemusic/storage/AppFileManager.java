package com.hardcodecoder.pulsemusic.storage;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppFileManager {

    private static final String TAG = "AppStorageManager";
    private static final Map<String, Integer> mHistoryMap = new HashMap<>();
    private static Set<String> mFavoritesSet = null;
    private static String mFilesDir;

    public static void initDataDir(@NonNull Context context) {
        mFilesDir = context.getFilesDir().getAbsolutePath();
        String[] fileNames = new File(StorageStructure.getAbsoluteHistoryPath(mFilesDir)).list();
        if (null != fileNames && fileNames.length > 20) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (null != jobScheduler)
                jobScheduler.schedule(new JobInfo.Builder(
                        3500,
                        new ComponentName(context, MaintenanceJob.class))
                        .build());
        }

        TaskRunner.executeAsync(() -> {
            File temp = new File(StorageStructure.getAbsoluteHistoryPath(mFilesDir));
            if (!temp.exists() && !temp.mkdir())
                Log.e(TAG, "Cannot create directory: " + temp.getAbsolutePath());
            temp = new File(StorageStructure.getAbsoluteFavoritesPath(mFilesDir));
            if (!temp.exists() && !temp.mkdir())
                Log.e(TAG, "Cannot create directory: " + temp.getAbsolutePath());
            temp = new File(StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir));
            if (!temp.exists() && !temp.mkdir())
                Log.e(TAG, "Cannot create directory: " + temp.getAbsolutePath());
        });
    }

    public static void addItemToHistory(@NonNull MusicModel md) {
        TaskRunner.executeAsync(() -> {
            Integer count = mHistoryMap.get(md.getTrackName());
            if (null == count) count = 1;
            else count++;
            StorageUtils.writeRawHistory(
                    StorageStructure.getAbsoluteHistoryPath(mFilesDir),
                    md,
                    count);

            mHistoryMap.put(md.getTrackName(), count);
        });
    }

    public static void getHistory(boolean defSort, @NonNull Callback<List<HistoryModel>> callback) {
        TaskRunner.executeAsync(() -> {
            String dirPth = StorageStructure.getAbsoluteHistoryPath(mFilesDir);
            File[] files = new File(dirPth).listFiles();
            List<HistoryModel> historyList = new ArrayList<>();
            if (null != files) {
                if (defSort)
                    StorageUtils.sortFiles(files);
                for (File file : files) {
                    HistoryModel hm = StorageUtils.readRawHistory(file);
                    if (null != hm) {
                        historyList.add(hm);
                        mHistoryMap.put(file.getName(), hm.getPlayCount());
                    }
                }
            }
            callback.onComplete(historyList);
        });
    }

    public static void addItemToFavorites(@NonNull MusicModel item) {
        if (null == mFavoritesSet)
            mFavoritesSet = new HashSet<>();
        if (mFavoritesSet.add(item.getTrackName()))
            TaskRunner.executeAsync(() -> {
                String filePath = StorageStructure.getAbsoluteFavoritesPath(mFilesDir).concat(item.getTrackName());
                boolean res = StorageUtils.writeRawFavorite(filePath);
                if (!res) Log.e(TAG, "Error writing favorites");
            });
    }

    private static void loadFavorites() {
        String favoritesDir = StorageStructure.getAbsoluteFavoritesPath(mFilesDir);
        String[] files = new File(favoritesDir).list();
        mFavoritesSet = new HashSet<>();
        if (null != files) {
            Collections.addAll(mFavoritesSet, files);
        }
    }

    public static void getFavorites(@NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            if (null == mFavoritesSet) loadFavorites();
            List<String> favoritesRaw = new ArrayList<>(mFavoritesSet);
            List<MusicModel> favorites = DataModelHelper.getModelsObjectFromTitlesList(favoritesRaw);
            callback.onComplete(favorites);
        });
    }

    public static void deleteFavorite(@NonNull MusicModel md) {
        if (null == mFavoritesSet)
            mFavoritesSet = new HashSet<>();
        if (mFavoritesSet.remove(md.getTrackName()))
            TaskRunner.executeAsync(() -> {
                // If true item has been removed successfully.
                // Remove from database as well
                if (!new File(StorageStructure.getAbsoluteFavoritesPath(
                        mFilesDir) + md.getTrackName()).delete())
                    Log.e(TAG, "Error deleting favorite");
            });
    }

    public static void isItemAFavorite(@NonNull MusicModel item, @NonNull Callback<Boolean> callback) {
        if (null == mFavoritesSet) {
            Handler handler = new Handler();
            TaskRunner.executeAsync(() -> {
                loadFavorites();
                handler.post(() -> callback.onComplete(mFavoritesSet.contains(item.getTrackName())));
            });
            loadFavorites();
        } else callback.onComplete(mFavoritesSet.contains(item.getTrackName()));
    }

    public static void savePlaylist(@NonNull String playlistName) {
        TaskRunner.executeAsync(() -> {
            boolean res = StorageUtils.writeRawPlaylist(
                    StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                            playlistName);
            if (!res) Log.e(TAG, "Error creating playlist");
        });
    }

    public static void getPlaylists(@NonNull Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(
                    StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir))
                    .listFiles();
            List<String> playlistTitles = null;
            if (null != files && files.length > 0) {
                StorageUtils.sortFiles(files);
                playlistTitles = new ArrayList<>();
                for (File file : files)
                    playlistTitles.add(file.getName());
            }
            callback.onComplete(playlistTitles);
        });
    }

    public static void renamePlaylist(@NonNull String oldPlaylistName, @NonNull String newPlaylistName) {
        TaskRunner.executeAsync(() -> {
            String playlistDir = StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir);
            File oldName = new File(playlistDir + oldPlaylistName);
            File newName = new File(playlistDir + newPlaylistName);
            if (!oldName.renameTo(newName))
                Log.e(TAG, "Unable to rename playlist from: " + oldName.getAbsolutePath() + ", to: " + newName.getAbsolutePath());
        });
    }

    public static void addItemToPlaylist(@NonNull String playlistName, @NonNull MusicModel itemToAdd) {
        TaskRunner.executeAsync(() -> StorageUtils.writeTrackToPlaylist(
                StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                        playlistName, itemToAdd.getTrackName()));
    }

    public static void addItemsToPlaylist(@NonNull String playlistName,
                                          @NonNull List<MusicModel> playlistTracks,
                                          @Nullable Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> tracksTitleRaw = DataModelHelper.getTitlesListFromModelsObject(playlistTracks);
            StorageUtils.writeTracksToPlaylist(
                    StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                            playlistName, tracksTitleRaw);
            if (null != callback) callback.onComplete(true);
        });
    }

    public static void getPlaylistTracks(@NonNull String playlistName, @NonNull Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            String playlistPath = StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir);
            List<String> playlistTracksRaw = StorageUtils.readRawPlaylistTracks(playlistPath + playlistName);
            List<MusicModel> playlistTracks = DataModelHelper.getModelsObjectFromTitlesList(playlistTracksRaw);
            callback.onComplete(playlistTracks);
        });
    }

    public static void updatePlaylistItems(@NonNull String playlistName, @NonNull List<MusicModel> newTracksList) {
        TaskRunner.executeAsync(() -> {
            deletePlaylist(playlistName);
            addItemsToPlaylist(playlistName, newTracksList, null);
        });
    }

    public static void deletePlaylist(@NonNull String playlistName) {
        if (!new File(StorageStructure.getAbsolutePlaylistsFolderPath(
                mFilesDir) +
                playlistName).delete())
            Log.e(TAG, "Error deleting playlist");
    }

    public static File getPlaylistFolderFile() {
        return new File(StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir));
    }
}
