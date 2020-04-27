package com.hardcodecoder.pulsemusic.helper;

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
}
