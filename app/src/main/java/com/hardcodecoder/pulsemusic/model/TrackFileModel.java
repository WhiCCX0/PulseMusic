package com.hardcodecoder.pulsemusic.model;

public class TrackFileModel {

    private String mDisplayName;
    private String mFileType;
    private long mFileSize;
    private int mBitRate;
    private int mSampleRate;
    private int mChannelCount;

    public TrackFileModel(String mDisplayName, String mFileType, long mFileSize, int mBitRate, int mSampleRate, int mChannelCount) {
        this.mDisplayName = mDisplayName;
        this.mFileType = mFileType;
        this.mFileSize = mFileSize;
        this.mBitRate = mBitRate;
        this.mSampleRate = mSampleRate;
        this.mChannelCount = mChannelCount;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getFileType() {
        return mFileType;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public int getBitRate() {
        return mBitRate;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getChannelCount() {
        return mChannelCount;
    }
}
