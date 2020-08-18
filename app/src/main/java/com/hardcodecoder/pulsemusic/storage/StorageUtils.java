package com.hardcodecoder.pulsemusic.storage;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.model.HistoryModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class StorageUtils {

    private static final String TAG = StorageUtils.class.getSimpleName();
    private static final int LINE_ID_ALBUM = 0;
    private static final int LINE_ID_ARTIST = 1;
    private static final int LINE_ID_ALBUM_ID = 2;
    private static final int LINE_ID_LAST_MODIFIED = 3;
    private static final int LINE_ID_PLAY_COUNT = 4;

    @SuppressLint("DefaultLocale")
    private static String getWriteableHistoryDataFrom(MusicModel md, int playCount) {
        long modifiedTime = SystemClock.elapsedRealtime();
        return String.format("%s\n%s\n%d\n%d\n%d",
                md.getAlbum(),
                md.getArtist(),
                md.getAlbumId(),
                modifiedTime,
                playCount);
    }

    private static HistoryModel getHistoryModelFrom(String fileName, String[] lines) {
        return new HistoryModel(
                fileName,
                lines[LINE_ID_ALBUM],
                lines[LINE_ID_ARTIST],
                Long.parseLong(lines[LINE_ID_ALBUM_ID]),
                Long.parseLong(lines[LINE_ID_LAST_MODIFIED]),
                Integer.parseInt(lines[LINE_ID_PLAY_COUNT])
        );
    }

    static void sortFiles(File[] files) {
        Arrays.sort(files, (o1, o2) -> {
            long d = o2.lastModified() - o1.lastModified();
            if (d > 1) return 1;
            else if (d == 0) return 0;
            else return -1;
        });
    }

    static void writeRawHistory(String historyDir, MusicModel data, int playCount) {
        FileOutputStream fos = null;
        try {
            File file = new File(historyDir + data.getTrackName());
            fos = new FileOutputStream(file);
            fos.write(StorageUtils.getWriteableHistoryDataFrom(data, playCount).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    static HistoryModel readRawHistory(File file) {
        try {
            int index = 0;
            String[] lines = new String[5];
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null)
                lines[index++] = line;
            reader.close();
            return getHistoryModelFrom(file.getName(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void writeRawFavorite(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.createNewFile())
                Log.e(TAG, "Unable to create favorite item file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void writeRawPlaylist(String filePath) {
        try {
            File playlistFile = new File(filePath);
            if (!playlistFile.createNewFile())
                Log.e(TAG, "Unable to create playlist item file: " + playlistFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    static List<String> readRawPlaylistTracks(String filePath) {
        List<String> playlistTracks = new ArrayList<>();
        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                playlistTracks.add(line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlistTracks;
    }

    static void writeTrackToPlaylist(String filePath, String trackTitle) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            fos = new FileOutputStream(file, true);
            fos.write(trackTitle.concat("\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void writeTracksToPlaylist(String filePath, List<String> playlistTracks) {
        if (null != playlistTracks && playlistTracks.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String line : playlistTracks) {
                builder.append(line);
                builder.append("\n");
            }
            FileOutputStream fos = null;
            try {
                File file = new File(filePath);
                fos = new FileOutputStream(file, true);
                fos.write(builder.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static void createDir(File file) {
        if (!file.exists() && !file.mkdir())
            Log.e(TAG, "Cannot create directory: " + file.getAbsolutePath());
    }

    static void renameFile(File renameFrom, File renameTo) {
        if (!renameFrom.renameTo(renameTo))
            Log.e(TAG, "Unable to rename file from: " + renameFrom.getAbsolutePath() + ", to: " + renameTo.getAbsolutePath());
    }

    static void deleteFile(File file) {
        if (!file.delete())
            Log.e(TAG, "Unable to delete file: " + file.getAbsolutePath());
    }
}
