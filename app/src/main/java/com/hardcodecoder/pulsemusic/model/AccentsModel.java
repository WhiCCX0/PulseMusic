package com.hardcodecoder.pulsemusic.model;

import androidx.annotation.ColorInt;

public class AccentsModel {

    private String mTitle;
    private int mAccentId;
    @ColorInt
    private int mColor;

    public AccentsModel(int id, String title, int color) {
        this.mAccentId = id;
        this.mTitle = title;
        this.mColor = color;
    }

    public int getId() {
        return mAccentId;
    }

    public String getTitle() {
        return mTitle;
    }

    @ColorInt
    public int getColor() {
        return mColor;
    }
}
