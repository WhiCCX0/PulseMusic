package com.hardcodecoder.pulsemusic.storage;

import java.io.File;

class StorageStructure {

    private static final String FAVORITES_FILE = "favorites";
    private static final String HISTORY_FILE = "history";
    private static final String PLAYLISTS_FOLDER = "playlists";

    static String getAbsoluteFavoritesPath(String filesDir) {
        return filesDir + File.separator + FAVORITES_FILE;
    }

    static String getAbsoluteHistoryPath(String filesDir) {
        return filesDir + File.separator + HISTORY_FILE;
    }

    static String getAbsolutePlaylistsFolderPath(String filesDir) {
        return filesDir + File.separator + PLAYLISTS_FOLDER + File.separator;
    }
}
