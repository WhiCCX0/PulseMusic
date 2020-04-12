package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {

    private static final String FOLDER_HISTORY = "history";
    private static final String PLAYLIST_DATA_FILE_NAME = "Playlist_Tracks_Data.data";
    private static final String PLAYLIST_TITLE_FILE_NAME = "Playlist_Names.data";
    private static final String FAVORITE_TRACKS_FILE_NAME = "FavoriteTracks.data";
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void addTrackToHistory(Context context, MusicModel md) {
        TaskRunner.getInstance().executeAsync(() -> DataWriter.writeToHistory(context.getFilesDir().getAbsolutePath(), md));
    }

    public static void addFavoritesList(Context context, List<MusicModel> list) {
        TaskRunner.getInstance().executeAsync(() -> DataWriter.writeFavorite(context.getFilesDir().getAbsolutePath(), list));
    }

    public static void addPlaylistTitles(Context context, List<String> stringList) {
        TaskRunner.getInstance().executeAsync(() -> DataWriter.savePlaylistTitles(context.getFilesDir().getAbsolutePath(), stringList));
    }

    public static void updatePlaylistTrackAt(Context context, List<MusicModel> updatedList, int index) {
        TaskRunner.getInstance().executeAsync(() -> DataWriter.modifyPlaylistTrackAt(context.getFilesDir().getAbsolutePath(), updatedList, index));
    }

    public static void deletePlaylistCardAt(Context context, int index) {
        updatePlaylistTrackAt(context, null, index);
    }

    public static void deleteAllPlaylists(Context context) {
        TaskRunner.getInstance().executeAsync(() -> DataWriter.deleteAllPlaylistData(context.getFilesDir().getAbsolutePath()));
    }


    public static void getSavedHistoryAsync(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            List<MusicModel> returnedList = DataReader.readSavedHistory(context.getFilesDir().getAbsolutePath());
            handler.post(() -> callback.onComplete(returnedList));
        });
    }

    public static void getSavedFavoriteTracksAsync(Context context, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            List<MusicModel> returnedList = DataReader.readSavedFavorites(context.getFilesDir().getAbsolutePath());
            handler.post(() -> callback.onComplete(returnedList));
        });
    }

    public static void getPlaylistTitlesListAsync(Context context, TaskRunner.Callback<List<String>> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            List<String> returnedList = DataReader.readPlaylistTiles(context.getFilesDir().getAbsolutePath());
            if (returnedList.size() == 0) {
                returnedList.add(context.getString(R.string.playlist_current_queue));
                returnedList.add(context.getString(R.string.favorite_playlist));
            }
            handler.post(() -> callback.onComplete(returnedList));
        });
    }

    private static void getAllPlaylistTracksAsync(String baseDirPath, TaskRunner.Callback<List<List<MusicModel>>> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            List<List<MusicModel>> returnedList = DataReader.readAllPlaylistTracks(baseDirPath);
            handler.post(() -> callback.onComplete(returnedList));
        });
    }

    public static void getPlaylistDataAtAsync(Context context, int index, TaskRunner.Callback<List<MusicModel>> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            List<MusicModel> returnedList = DataReader.readPlaylistTrackAtPosition(context.getFilesDir().getAbsolutePath(), index);
            handler.post(() -> callback.onComplete(returnedList));

        });
    }

    private static class DataWriter {

        private static final String TAG = "DataWriter";

        private static void writeToHistory(String baseDirPath, MusicModel md) {
            File baseDir = new File(baseDirPath + File.separator + FOLDER_HISTORY);
            if (baseDir.exists()) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(baseDir.getAbsolutePath() + File.separator + md.getSongName());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(md);
                    objectOutputStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (baseDir.mkdir())
                writeToHistory(baseDirPath, md);
            else Log.e(TAG, "Cannot create directory in internal storage :" + FOLDER_HISTORY);
        }

        private static void writeFavorite(String baseDirPath, List<MusicModel> list) {
            if (null != list && list.size() > 0) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(baseDirPath + File.separator + FAVORITE_TRACKS_FILE_NAME));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(list);
                    objectOutputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private static void savePlaylistTitles(String baseDirPath, List<String> titles) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(baseDirPath + File.separator + PLAYLIST_TITLE_FILE_NAME));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(titles);
                objectOutputStream.close();
                outputStream.close();
            } catch (Exception e) {
                LogHelper(PLAYLIST_TITLE_FILE_NAME);
            }
        }

        private static void modifyPlaylistTrackAt(String baseDirPath, List<MusicModel> updatedList, int index) {
            getAllPlaylistTracksAsync(baseDirPath, allPlaylistTracks -> {
                int l = allPlaylistTracks.size();
                if (l > 0) {
                    if (index < l) allPlaylistTracks.remove(index);
                    if (updatedList != null) allPlaylistTracks.add(index, updatedList);
                } else allPlaylistTracks.add(index, updatedList);

                writeAllPlaylistData(baseDirPath, allPlaylistTracks);
            });
        }

        private static void deleteAllPlaylistData(String baseDirPath) {
            try {
                File f = new File(baseDirPath + File.separator + PLAYLIST_TITLE_FILE_NAME);
                if (!f.delete())
                    Log.e(TAG, "Unable to delete playlist title data file");
                f = new File(baseDirPath + File.separator + PLAYLIST_DATA_FILE_NAME);
                if (!f.delete())
                    Log.e(TAG, "Unable to delete playlist list data file");
            } catch (Exception e) {
                LogHelper(PLAYLIST_TITLE_FILE_NAME + "and" + PLAYLIST_DATA_FILE_NAME);
            }
        }

        private static void writeAllPlaylistData(String baseDirPath, List<List<MusicModel>> listOfLists) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(baseDirPath + File.separator + PLAYLIST_DATA_FILE_NAME));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(listOfLists);
                objectOutputStream.close();
                outputStream.close();
            } catch (Exception e) {
                LogHelper(PLAYLIST_DATA_FILE_NAME);
            }
        }

        private static void LogHelper(String file) {
            Log.v(TAG, "File not found : " + file);
        }
    }

    private static class DataReader {

        private static final String TAG = "DataReader";

        private static List<MusicModel> readSavedHistory(String baseDirPath) {
            File f = new File(baseDirPath + File.separator + FOLDER_HISTORY + File.separator);
            File[] files = f.listFiles();
            List<MusicModel> list = new ArrayList<>();
            if (null != files) {
                Arrays.sort(files, (o1, o2) -> (int) (o2.lastModified() - o1.lastModified()));
                for (File file : files) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        list.add((MusicModel) objectInputStream.readObject());
                        objectInputStream.close();
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        private static List<MusicModel> readSavedFavorites(String baseDirPath) {
            List<MusicModel> list = new ArrayList<>();
            try {
                FileInputStream inputStream = new FileInputStream(new File(baseDirPath + File.separator + FAVORITE_TRACKS_FILE_NAME));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                list = ((List<MusicModel>) objectInputStream.readObject());
                objectInputStream.close();
                inputStream.close();
            } catch (Exception e) {
                LogHelper(FAVORITE_TRACKS_FILE_NAME);
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        private static List<String> readPlaylistTiles(String baseDirPath) {
            List<String> list = new ArrayList<>();
            try {
                FileInputStream inputStream = new FileInputStream(new File(baseDirPath + File.separator + PLAYLIST_TITLE_FILE_NAME));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                list.clear();
                list.addAll((List<String>) objectInputStream.readObject());
                objectInputStream.close();
                inputStream.close();
            } catch (Exception e) {
                LogHelper(PLAYLIST_TITLE_FILE_NAME);
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        private static List<List<MusicModel>> readAllPlaylistTracks(String baseDirPAth) {
            List<List<MusicModel>> listOfList = new ArrayList<>();
            try {
                FileInputStream inputStream = new FileInputStream(new File(baseDirPAth + File.separator + PLAYLIST_DATA_FILE_NAME));
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                listOfList = (List<List<MusicModel>>) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
            } catch (Exception e) {
                LogHelper(PLAYLIST_DATA_FILE_NAME);
            }
            return listOfList;
        }

        private static List<MusicModel> readPlaylistTrackAtPosition(String baseDirPath, int pos) {
            List<List<MusicModel>> listOfList = readAllPlaylistTracks(baseDirPath);
            List<MusicModel> mList = new ArrayList<>();
            if (pos < listOfList.size()) {
                mList.addAll(listOfList.get(pos));
                return mList;
            } else
                return mList;
        }

        private static void LogHelper(String file) {
            Log.v(TAG, "File not found : " + file);
        }
    }

}
