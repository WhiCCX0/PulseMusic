package com.hardcodecoder.pulsemusic.storage;

import java.io.File;

class StorageStructure {

    private static final String FAVORITES_FOLDER = "favorites";
    private static final String HISTORY_FOLDER = "history";
    private static final String PLAYLISTS_FOLDER = "playlists";

    static String getAbsoluteFavoritesPath(String filesDir) {
        return filesDir + File.separator + FAVORITES_FOLDER + File.separator;
    }

    static String getAbsoluteHistoryPath(String filesDir) {
        return filesDir + File.separator + HISTORY_FOLDER + File.separator;
    }

    static String getAbsolutePlaylistsFolderPath(String filesDir) {
        return filesDir + File.separator + PLAYLISTS_FOLDER + File.separator;
    }
}
