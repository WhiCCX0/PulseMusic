package com.hardcodecoder.pulsemusic.storage;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
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

    private static final Map<String, Integer> mHistoryMap = new HashMap<>();
    private static Set<String> mFavoritesSet = null;
    private static String mFilesDir;

    public static void initDataDir(@NonNull Context context) {
        mFilesDir = context.getFilesDir().getAbsolutePath();
        deleteOldHistoryFiles();
        TaskRunner.executeAsync(() -> {
            StorageUtils.createDir(new File(StorageStructure.getAbsoluteHistoryPath(mFilesDir)));
            StorageUtils.createDir(new File(StorageStructure.getAbsoluteFavoritesPath(mFilesDir)));
            StorageUtils.createDir(new File(StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir)));
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
            TaskRunner.executeAsync(() -> StorageUtils.writeRawFavorite(
                    StorageStructure.getAbsoluteFavoritesPath(mFilesDir)
                            .concat(item.getTrackName())));
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
        if (mFavoritesSet.remove(md.getTrackName())) {
            StorageUtils.deleteFile(
                    new File(StorageStructure.getAbsoluteFavoritesPath(
                            mFilesDir)
                            + md.getTrackName()));
        }
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
        TaskRunner.executeAsync(() ->
                StorageUtils.writeRawPlaylist(
                        StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir) +
                                playlistName));
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
            StorageUtils.renameFile(oldName, newName);
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
        StorageUtils.deleteFile(
                new File(StorageStructure.getAbsolutePlaylistsFolderPath(
                        mFilesDir) +
                        playlistName));
    }

    public static File getPlaylistFolderFile() {
        return new File(StorageStructure.getAbsolutePlaylistsFolderPath(mFilesDir));
    }

    public static void deleteObsoleteHistoryFiles() {
        TaskRunner.executeAsync(() -> {
            String hisToryDir = StorageStructure.getAbsoluteHistoryPath(mFilesDir);
            File[] files = new File(hisToryDir).listFiles();
            if (null != files && files.length > 0) {
                List<MusicModel> masterList = LoaderCache.getAllTracksList();
                if (null != masterList && masterList.size() > 0) {
                    Set<String> currentList = new HashSet<>();
                    for (MusicModel md : masterList) currentList.add(md.getTrackName());
                    for (File f : files)
                        if (!currentList.contains(f.getName())) StorageUtils.deleteFile(f);
                } else {
                    for (File f : files) StorageUtils.deleteFile(f);
                }
            }
        });
    }

    private static void deleteOldHistoryFiles() {
        TaskRunner.executeAsync(() -> {
            String hisToryDir = StorageStructure.getAbsoluteHistoryPath(mFilesDir);
            File[] files = new File(hisToryDir).listFiles();
            int size;
            if (null != files && (size = files.length) > 20) {
                // Sorts in descending order by modified date
                StorageUtils.sortFiles(files);
                File[] deleteFiles = new File[size - 20];
                System.arraycopy(files, 20, deleteFiles, 0, size - 20);
                for (File deleteFile : deleteFiles) StorageUtils.deleteFile(deleteFile);
            }
        });
    }
}
