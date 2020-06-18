package com.hardcodecoder.pulsemusic.storage;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class StorageManager {

    private static final String TAG = "StorageManager";

    void addTrackToHistory(String filesDir, String itemToAdd, int count) {
        String historyDirAPath = StorageStructure.getAbsoluteHistoryPath(filesDir);
        File historyDir = new File(historyDirAPath);
        if (historyDir.exists()) {
            File historyItem = new File(historyDirAPath + itemToAdd);
            try {
                if (historyItem.exists() || historyItem.createNewFile()) {
                    FileWriter writer = new FileWriter(historyItem);
                    writer.write(String.valueOf(count));
                    writer.close();
                } else
                    Log.e(TAG, "Cannot create file inside history directory: " + historyItem.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (historyDir.mkdir())
            addTrackToHistory(filesDir, itemToAdd, count);
    }

    void addTrackToFavorites(String filesDir, String itemToAdd) {
        String favoritesDirPath = StorageStructure.getAbsoluteFavoritesPath(filesDir);
        File favoritesDir = new File(favoritesDirPath);
        if (favoritesDir.exists()) {
            File favoriteItem = new File(favoritesDirPath + itemToAdd);
            try {
                if (!favoriteItem.createNewFile())
                    Log.e(TAG, "Cannot create favorites item inside favorites directory");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (favoritesDir.mkdir())
            addTrackToFavorites(filesDir, itemToAdd);
    }

    @Nullable
    List<String> getSavedHistory(String filesDir) {
        String historyDirAPath = StorageStructure.getAbsoluteHistoryPath(filesDir);
        File historyDir = new File(historyDirAPath);
        File[] files;
        if (historyDir.exists() && null != (files = historyDir.listFiles())) {
            Arrays.sort(files, (o1, o2) -> {
                long d = o2.lastModified() - o1.lastModified();
                if (d > 1) return 1;
                else if (d == 0) return 0;
                else return -1;
            });
            List<String> recentList = new ArrayList<>();
            for (File file : files)
                recentList.add(file.getName());
            return recentList;
        }
        return null;
    }


    @Nullable
    List<String> getSavedFavoriteTracks(String filesDir) {
        String favoritesDirPath = StorageStructure.getAbsoluteFavoritesPath(filesDir);
        File favoritesDir = new File(favoritesDirPath);
        if (favoritesDir.exists()) {
            List<String> favoritesList = new ArrayList<>();
            File[] files = favoritesDir.listFiles();
            if (null != files) {
                for (File file : files)
                    favoritesList.add(file.getName());
            }
            return favoritesList;
        }
        return null;
    }

    void savePlaylist(String filesDir, String playlistName) {
        File basePlaylistsDir = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir));
        if (basePlaylistsDir.exists()) {
            File playlistFile = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + playlistName);
            try {
                if (!playlistFile.createNewFile())
                    Log.e(TAG, "Cannot create new file: " + playlistFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (basePlaylistsDir.mkdir())
            savePlaylist(filesDir, playlistName);
    }

    List<String> getPlaylists(String filesDir) {
        File[] files = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir)).listFiles();
        List<String> playlistTitles = new ArrayList<>();
        if (null != files && files.length > 0) {
            Arrays.sort(files, (o1, o2) -> {
                long d = o1.lastModified() - o2.lastModified();
                if (d > 1) return 1;
                else if (d == 0) return 0;
                else return -1;
            });
            for (File file : files) {
                playlistTitles.add(file.getName());
            }
        }
        return playlistTitles;
    }

    void renamePlaylist(String filesDir, String oldPlaylistName, String newPlaylistName) {
        File oldName = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + oldPlaylistName);
        File newName = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + newPlaylistName);
        if (!oldName.renameTo(newName))
            Log.e(TAG, "Unable to rename playlist from: " + oldName.getAbsolutePath() + ", to: " + newName.getAbsolutePath());
    }

    void addTracksToPlaylist(String filesDir, String playlistName, List<String> playlistTracks) {
        if (null != playlistTracks && playlistTracks.size() > 0) {
            try {
                FileWriter writer = new FileWriter(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + playlistName, true);
                for (String str : playlistTracks)
                    writer.write(str + System.lineSeparator());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    List<String> getPlaylistTracks(String filesDir, String playlistName) {
        List<String> playlistTracks = new ArrayList<>();
        try {
            Scanner s = new Scanner(new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + playlistName));
            while (s.hasNextLine())
                playlistTracks.add(s.nextLine());
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlistTracks;
    }

    void deleteTrackFromFavoritesList(String filesDir, String itemToDelete) {
        File file = new File(StorageStructure.getAbsoluteFavoritesPath(filesDir) + itemToDelete);
        if (!file.delete())
            Log.e(TAG, "Unable to delete favorite item file: " + itemToDelete);
    }

    void deletePlaylist(String filesDir, String playlistName) {
        File playlist = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + playlistName);
        if (playlist.exists())
            if (!playlist.delete())
                Log.e(TAG, "Cannot delete playlist: " + playlist.getAbsolutePath());
    }

    void updatePlaylistTracks(String filesDir, String playlistName, List<String> newTracksList) {
        //Delete already existing playlist
        deletePlaylist(filesDir, playlistName);
        //Write updated playlist data
        addTracksToPlaylist(filesDir, playlistName, newTracksList);
    }
}
