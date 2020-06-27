package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.res.Resources;

import com.hardcodecoder.pulsemusic.R;

public class DimensionsUtil {

    private static int mRoundingRadius2dp;
    private static int mRoundingRadius4dp;
    private static int mRoundingRadius8dp;
    private static int mRoundingRadius16dp;

    public static void init(Context context) {
        Resources resources = context.getResources();
        mRoundingRadius2dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_2dp);
        mRoundingRadius4dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_4dp);
        mRoundingRadius8dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_8dp);
        mRoundingRadius16dp = resources.getDimensionPixelSize(R.dimen.rounding_radius_16dp);
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

    public static int getRoundingRadiusPixelSize16dp() {
        return mRoundingRadius16dp;
    }

    public static int getRoundingRadius(RoundingRadius r) {
        switch (r) {
            case RADIUS_2dp:
                return DimensionsUtil.getRoundingRadiusPixelSize2dp();
            case RADIUS_4dp:
                return DimensionsUtil.getRoundingRadiusPixelSize4dp();
            case RADIUS_8dp:
                return DimensionsUtil.getRoundingRadiusPixelSize8dp();
            case RADIUS_16dp:
                return DimensionsUtil.getRoundingRadiusPixelSize16dp();
            case RADIUS_NONE:
            default:
                return 0;
        }
    }

    public enum RoundingRadius {
        RADIUS_NONE,
        RADIUS_2dp,
        RADIUS_4dp,
        RADIUS_8dp,
        RADIUS_16dp,
    }
}
