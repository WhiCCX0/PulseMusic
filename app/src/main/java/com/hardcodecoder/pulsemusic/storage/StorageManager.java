package com.hardcodecoder.pulsemusic.storage;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class StorageManager {

    private static final String TAG = "StorageManager";

    private StorageManager() {
    }

    static void addTrackToHistory(String filesDir, String itemToAdd) {
        TaskRunner.executeAsync(() -> Writer.writeToHistory(filesDir, itemToAdd));
    }

    static void addTrackToFavorites(String filesDir, String itemToAdd) {
        TaskRunner.executeAsync(() -> Writer.writeFavorite(filesDir, itemToAdd));
    }

    static void getSavedHistory(String filesDir, Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> callback.onComplete(Reader.readSavedHistory(filesDir)));
    }

    static void getSavedFavoriteTracks(String filesDir, Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> callback.onComplete(Reader.readSavedFavorites(filesDir)));
    }

    static void savePlaylist(String filesDir, String playlistName) {
        TaskRunner.executeAsync(() -> Writer.writePlaylist(filesDir, playlistName));
    }

    static void getPlaylists(String filesDir, Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> callback.onComplete(Reader.readPlaylists(filesDir)));
    }

    static void renamePlaylist(String filesDir, String oldPlaylistName, String newPlaylistName) {
        TaskRunner.executeAsync(() -> {
            File oldName = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + oldPlaylistName);
            File newName = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + newPlaylistName);
            if (!oldName.renameTo(newName))
                Log.e(TAG, "Unable to rename playlist from: " + oldName.getAbsolutePath() + ", to: " + newName.getAbsolutePath());
        });
    }

    static void addTracksToPlaylist(String filesDir, String playlistName, List<String> playlistTracks) {
        TaskRunner.executeAsync(() -> Writer.writePlaylistTracks(filesDir, playlistName, playlistTracks));
    }

    static void getPlaylistTracks(String filesDir, String playlistName, Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> callback.onComplete(Reader.readPlaylistContent(filesDir, playlistName)));
    }

    static void deleteTrackFromFavoritesList(String filesDir, String itemToDelete) {
        TaskRunner.executeAsync(() -> Writer.deleteTrackFavoritesList(filesDir, itemToDelete));
    }

    static void deletePlaylist(String filesDir, String playlistName) {
        TaskRunner.executeAsync(() -> Writer.deletePlaylist(filesDir, playlistName));
    }

    static void updatePlaylistTracks(String filesDir, String playlistName, List<String> newTracksList) {
        TaskRunner.executeAsync(() -> {
            //Delete already existing playlist
            Writer.deletePlaylist(filesDir, playlistName);
            //Write updated playlist data
            Writer.writePlaylistTracks(filesDir, playlistName, newTracksList);
        });
    }

    private static class Writer {

        private static final String TAG = "DataWriter";

        private static void writeToHistory(String filesDir, String itemToAdd) {
            try {
                FileWriter writer = new FileWriter(StorageStructure.getAbsoluteHistoryPath(filesDir), true);
                writer.write(itemToAdd + System.lineSeparator());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void writeFavorite(String filesDir, String itemToAdd) {
            String favoritesDirPath = StorageStructure.getAbsoluteFavoritesPath(filesDir);
            File favoritesDir = new File(favoritesDirPath);
            if (favoritesDir.exists()) {
                File favoriteItem = new File(favoritesDirPath + itemToAdd);
                try {
                    if (!favoriteItem.createNewFile())
                        Log.e(TAG, "Error creating favorites item inside favorites directory");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (favoritesDir.mkdir())
                writeFavorite(filesDir, itemToAdd);
        }

        private static boolean writePlaylist(String filesDir, String playListName) {
            File basePlaylistsDir = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir));
            if (basePlaylistsDir.exists()) {
                File playlistFile = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + playListName);
                try {
                    return playlistFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (basePlaylistsDir.mkdir())
                return writePlaylist(filesDir, playListName);
            return false;
        }

        private static void writePlaylistTracks(String filesDir, String playlistName, List<String> playlistTracks) {
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

        private static void deleteTrackFavoritesList(String filesDir, String itemToDelete) {
            File file = new File(StorageStructure.getAbsoluteFavoritesPath(filesDir) + itemToDelete);
            if (!file.delete())
                Log.e(TAG, "Unable to delete favorite item file: " + itemToDelete);
        }

        private static void deletePlaylist(String filesDir, String playlistName) {
            File playlist = new File(StorageStructure.getAbsolutePlaylistsFolderPath(filesDir) + playlistName);
            if (playlist.exists())
                if (!playlist.delete())
                    Log.e(TAG, "Error deleting playlist: " + playlist.getAbsolutePath());
        }
    }

    private static class Reader {

        @Nullable
        private static List<String> readSavedHistory(String filesDir) {
            String absolutePath = StorageStructure.getAbsoluteHistoryPath(filesDir);
            File file = new File(absolutePath);
            if (file.exists()) {
                List<String> recentList = new ArrayList<>();
                try {
                    Scanner s = new Scanner(file);
                    while (s.hasNextLine())
                        recentList.add(s.nextLine());
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return recentList;
            }
            return null;
        }

        @Nullable
        private static List<String> readSavedFavorites(String filesDir) {
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

        @NonNull
        private static List<String> readPlaylists(String filesDir) {
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

        private static List<String> readPlaylistContent(String filesDir, String playlistName) {
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
    }

}
