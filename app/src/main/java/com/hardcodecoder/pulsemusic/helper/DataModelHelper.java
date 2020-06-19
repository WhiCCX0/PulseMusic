package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TrackFileModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModelHelper {

    private static int mPickedTrackId = 0;

    public static List<MusicModel> getModelsObjectFromTitlesList(List<String> titles) {
        Map<String, MusicModel> modelMap = new HashMap<>();
        for (MusicModel musicModel : LoaderCache.getAllTracksList())
            modelMap.put(musicModel.getTrackName(), musicModel);

        List<MusicModel> modelList = new ArrayList<>();
        for (String name : titles) {
            if (null != modelMap.get(name))
                modelList.add(modelMap.get(name));
        }
        return modelList;
    }

    public static List<String> getTitlesListFromModelsObject(List<MusicModel> modelList) {
        List<String> titlesList = new ArrayList<>();
        for (MusicModel musicModel : modelList) {
            titlesList.add(musicModel.getTrackName());
        }
        return titlesList;
    }

    public static MusicModel buildMusicModelFrom(Context context, Intent data) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, data.getData());
        try {
            String path = data.getDataString();
            if (path != null) {
                String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                int duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                mmr.release();
                mPickedTrackId--;
                return new MusicModel(mPickedTrackId, name, path, album, artist, null, mPickedTrackId, duration);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static void getTrackInfo(Context context, MusicModel musicModel, TaskRunner.Callback<TrackFileModel> callback) {
        TaskRunner.executeAsync(() -> {
            Uri uri = Uri.parse(musicModel.getTrackPath());
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri, null, null, null, null);

            if (null != cursor && cursor.moveToFirst()) {
                MediaExtractor mediaExtractor = new MediaExtractor();
                try {
                    mediaExtractor.setDataSource(context, uri, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                String displayName = cursor.getString(nameIndex);
                long fileSize = cursor.getLong(sizeIndex);
                String mimeType = context.getContentResolver().getType(uri);

                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(0);
                int bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE);
                int sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                int channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                cursor.close();
                TrackFileModel trackFileModel = new TrackFileModel(displayName, mimeType, fileSize, bitRate, sampleRate, channelCount);
                callback.onComplete(trackFileModel);
            }
        });
    }
}
