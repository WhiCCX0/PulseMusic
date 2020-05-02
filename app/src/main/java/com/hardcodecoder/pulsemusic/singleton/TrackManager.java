package com.hardcodecoder.pulsemusic.singleton;

import android.content.Context;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.playback.PlaybackManager;
import com.hardcodecoder.pulsemusic.storage.StorageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackManager {

    private static final TrackManager ourInstance = new TrackManager();
    private Map<String, MusicModel> modelMap = new HashMap<>();
    private List<MusicModel> mActiveList = new ArrayList<>();
    private List<MusicModel> mMainList;
    private int mIndex = -1;
    private boolean mRepeatCurrentTrack = false;

    private TrackManager() {
    }

    public static TrackManager getInstance() {
        return ourInstance;
    }

    public List<MusicModel> getMainList() {
        return mMainList;
    }

    public void setMainList(final List<MusicModel> mainList) {
        mMainList = mainList;
        TaskRunner.executeAsync(() -> {
            for (MusicModel musicModel : mainList) {
                modelMap.put(musicModel.getSongName(), musicModel);
            }
        });
    }

    public Map<String, MusicModel> getModelMap() {
        return modelMap;
    }

    public void buildDataList(List<MusicModel> newList, int index) {
        mActiveList.clear();
        mActiveList.addAll(newList);
        setActiveIndex(index);
    }

    public int getActiveIndex() {
        return mIndex;
    }

    private void setActiveIndex(int index) {
        mIndex = index;
    }

    public List<MusicModel> getActiveQueue() {
        return mActiveList;
    }

    public MusicModel getActiveQueueItem() {
        return mActiveList.get(mIndex);
    }

    public void repeatCurrentTrack(boolean b) {
        mRepeatCurrentTrack = b;
    }

    public boolean isCurrentTrackInRepeatMode() {
        return mRepeatCurrentTrack;
    }

    public boolean canSkipTrack(short direction) {
        if (mRepeatCurrentTrack) {
            mRepeatCurrentTrack = false;
            return true;
        }
        if (direction == PlaybackManager.ACTION_PLAY_NEXT && mIndex < mActiveList.size() - 1) {
            setActiveIndex(++mIndex);
            return true;
        } else if (direction == PlaybackManager.ACTION_PLAY_PREV && mIndex > 0) {
            setActiveIndex(--mIndex);
            return true;
        }
        return false;
    }

    public void playNext(MusicModel md) {
        if (mIndex + 1 < mActiveList.size()) {
            if (mActiveList.get(mIndex + 1).getId() != md.getId()) {
                mActiveList.add(mIndex + 1, md);
            }
        } else
            mActiveList.add(mIndex + 1, md);
    }

    public void addToActiveQueue(MusicModel md) {
        mActiveList.add(md);
    }

    public boolean canRemoveItem(int position) {
        return position != mIndex;
    }

    public void updateActiveQueue(int from, int to) {
        if (mActiveList.size() > 0) {
            Collections.swap(mActiveList, from, to);
            if (mIndex == from)
                mIndex = to;
            else if (mIndex == to)
                mIndex = from;
        }
    }

    public void removeItemFromActiveQueue(int position) {
        if (mActiveList.size() > 0) {
            mActiveList.remove(position);
            if (position < getActiveIndex())
                mIndex--;
        }
    }

    public void restoreItem(int deletedQueueIndex, MusicModel musicModel) {
        mActiveList.add(deletedQueueIndex, musicModel);
    }

    public void addToHistory(Context context) {
        StorageHelper.addTrackToHistory(context, getActiveQueueItem());
    }
}
