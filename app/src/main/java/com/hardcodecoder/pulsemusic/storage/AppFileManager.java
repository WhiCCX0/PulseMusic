package com.hardcodecoder.pulsemusic.storage;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppFileManager {

    private static final String TAG = "AppStorageManager";
    private static final Map<String, Integer> mHistoryMap = new HashMap<>();
    private static final Set<String> mFavoritesSet = new HashSet<>();

    public static void initDataDir(Context context) {
        String filesDir = context.getFilesDir().getAbsolutePath();
        String[] fileNames = new File(StorageStructure.getAbsoluteHistoryPath(filesDir)).list();
        if (null != fileNames && fileNames.length > 20) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (null != jobScheduler)
                jobScheduler.schedule(new JobInfo.Builder(
                        3500,
                        new ComponentName(context, MaintenanceJob.class))
                        .build());
        }

        TaskRunner.executeAsync(() -> {
            File temp = new File(StorageStructure.getAbsoluteHistoryPath(filesDir));
            if (!temp.exists() && !temp.mkdir())
                Log.e(TAG, "Cannot create directory: " + temp.getAbsolutePath());
            temp = new File(StorageStructure.getAbsoluteFavoritesPath(filesDir));
            if (!temp.exists() && !temp.mkdir())
                Log.e(TAG, "Cannot create directory: " + temp.getAbsolutePath());
            temp = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir));
            if (!temp.exists() && !temp.mkdir())
                Log.e(TAG, "Cannot create directory: " + temp.getAbsolutePath());
        });
    }

    public static void addItemToHistory(Context context, MusicModel md) {
        TaskRunner.executeAsync(() -> {
            Integer count = mHistoryMap.get(md.getTrackName());
            if (null == count) count = 1;
            else count++;
            StorageUtils.writeRawHistory(
                    StorageStructure.getAbsoluteHistoryPath(context.getFilesDir().getAbsolutePath()),
                    md,
                    count);

            mHistoryMap.put(md.getTrackName(), count);
        });
    }

    public static void getHistory(Context context, boolean defSort, Callback<List<HistoryModel>> callback) {
        TaskRunner.executeAsync(() -> {
            String dirPth = StorageStructure.getAbsoluteHistoryPath(context.getFilesDir().getAbsolutePath());
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

    public static void addItemToFavorites(Context context, MusicModel item) {
        if (mFavoritesSet.add(item.getTrackName()))
            TaskRunner.executeAsync(() -> {
                String filePath = StorageStructure.getAbsoluteFavoritesPath(context.getFilesDir().getAbsolutePath()).concat(item.getTrackName());
                boolean res = StorageUtils.writeRawFavorite(filePath);
                if (!res) Log.e(TAG, "Error writing favorites");
            });
    }

    public static void getFavorites(Context context, Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            String favoritesDir = StorageStructure.getAbsoluteFavoritesPath(context.getFilesDir().getAbsolutePath());
            String[] files = new File(favoritesDir).list();
            List<String> favoritesList = new ArrayList<>();
            List<MusicModel> favorites = null;
            if (null != files) {
                for (String fileName : files) {
                    favoritesList.add(fileName);
                    mFavoritesSet.add(fileName);
                }
                favorites = DataModelHelper.getModelsObjectFromTitlesList(favoritesList);
            }
            callback.onComplete(favorites);
        });
    }

    public static void deleteFavorite(Context context, MusicModel md) {
        if (mFavoritesSet.remove(md.getTrackName()))
            TaskRunner.executeAsync(() -> {
                // If true item has been removed successfully.
                // Remove from database as well
                if (!new File(StorageStructure.getAbsoluteFavoritesPath(
                        context.getFilesDir().getAbsolutePath() +
                                md.getTrackName())).delete())
                    Log.e(TAG, "Error deleting favorite");
            });
    }

    public static boolean isItemAFavorite(MusicModel item) {
        return mFavoritesSet.contains(item.getTrackName());
    }

    public static void savePlaylist(Context context, String playlistName) {
        TaskRunner.executeAsync(() -> {
            boolean res = StorageUtils.writeRawPlaylist(
                    StorageStructure.getAbsolutePlaylistsFolderPath(context.getFilesDir().getAbsolutePath()) +
                            playlistName);
            if (!res) Log.e(TAG, "Error creating playlist");
        });
    }

    public static void getPlaylists(Context context, Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            File[] files = new File(
                    StorageStructure.getAbsolutePlaylistsFolderPath(context.getFilesDir().getAbsolutePath()))
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

    public static void renamePlaylist(Context context, String oldPlaylistName, String
            newPlaylistName) {
        TaskRunner.executeAsync(() -> {
            String playlistDir = StorageStructure.getAbsolutePlaylistsFolderPath(context.getFilesDir().getAbsolutePath());
            File oldName = new File(playlistDir + oldPlaylistName);
            File newName = new File(playlistDir + newPlaylistName);
            if (!oldName.renameTo(newName))
                Log.e(TAG, "Unable to rename playlist from: " + oldName.getAbsolutePath() + ", to: " + newName.getAbsolutePath());
        });
    }

    public static void addItemsToPlaylist(Context context, String
            playlistName, List<MusicModel> playlistTracks, Callback<Boolean> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> tracksTitleRaw = DataModelHelper.getTitlesListFromModelsObject(playlistTracks);
            StorageUtils.writeTracksToPlaylist(
                    StorageStructure.getAbsolutePlaylistsFolderPath(context.getFilesDir().getAbsolutePath()) +
                            playlistName, tracksTitleRaw);
            if (null != callback) callback.onComplete(true);
        });
    }

    public static void getPlaylistTracks(Context context, String
            playlistName, Callback<List<MusicModel>> callback) {
        TaskRunner.executeAsync(() -> {
            String playlistPath = StorageStructure.getAbsolutePlaylistsFolderPath(context.getFilesDir().getAbsolutePath());
            List<String> playlistTracksRaw = StorageUtils.readRawPlaylistTracks(playlistPath + playlistName);
            List<MusicModel> playlistTracks = DataModelHelper.getModelsObjectFromTitlesList(playlistTracksRaw);
            callback.onComplete(playlistTracks);
        });
    }

    public static void updatePlaylistItems(Context context, String
            playlistName, List<MusicModel> newTracksList) {
        TaskRunner.executeAsync(() -> {
            deletePlaylist(context, playlistName);
            addItemsToPlaylist(context, playlistName, newTracksList, null);
        });
    }

    public static void deletePlaylist(Context context, String playlistName) {
        if (!new File(StorageStructure.getAbsolutePlaylistsFolderPath(
                context.getFilesDir().getAbsolutePath()) +
                playlistName).delete())
            Log.e(TAG, "Error deleting playlist");
    }
}
