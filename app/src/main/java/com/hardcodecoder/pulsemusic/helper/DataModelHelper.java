package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;

import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataModelHelper {

    public static List<MusicModel> getModelFromTitle(List<String> titles) {
        List<MusicModel> modelList = new ArrayList<>();
        Map<String, MusicModel> modelMap = TrackManager.getInstance().getModelMap();
        for (String name : titles) {
            modelList.add(modelMap.get(name));
        }
        return modelList;
    }

    public static List<String> getTitleFromModel(List<MusicModel> modelList) {
        List<String> titlesList = new ArrayList<>();
        for (MusicModel musicModel : modelList) {
            titlesList.add(musicModel.getSongName());
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
                return new MusicModel(-1, name, artist, path, album, -1, null, duration);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
