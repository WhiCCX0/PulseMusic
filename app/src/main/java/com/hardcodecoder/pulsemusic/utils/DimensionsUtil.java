package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.res.Resources;

import com.hardcodecoder.pulsemusic.R;

public class DimensionsUtil {

    private static int mRoundingRadius2dp;
    private static int mRoundingRadius4dp;
    private static int mRoundingRadius8dp;
    private static int mRoundingRadius18dp;


    public static void init(Context context) {
        Resources resources = context.getResources();
        mRoundingRadius2dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_2dp);
        mRoundingRadius4dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_4dp);
        mRoundingRadius8dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_8dp);
        mRoundingRadius18dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_18dp);
    }


    public static int getRoundingRadiusPixelSize2dp() {
        return mRoundingRadius2dp;
    }

    public static int getRoundingRadiusPixelSize4dp() {
        return mRoundingRadius4dp;
    }

    public static int getRoundingRadiusPixelSize8dp() {
        return mRoundingRadius8dp;
    }

    public static int getRoundingRadiusPixelSize18dp() {
        return mRoundingRadius18dp;
    }
}
