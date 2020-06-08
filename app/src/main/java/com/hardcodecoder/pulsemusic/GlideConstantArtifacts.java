package com.hardcodecoder.pulsemusic;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class GlideConstantArtifacts {

    private static RoundedCorners mRadius2dp, mRadius4dp, mRadius8dp, mRadius16dp;
    private static CircleCrop mCircleCrop;
    private static CenterCrop mCenterCrop;

    public static RoundedCorners getRadius2dp() {
        if (null == mRadius2dp)
            mRadius2dp = new RoundedCorners(DimensionsUtil.getRoundingRadiusPixelSize2dp());
        return mRadius2dp;
    }

    public static RoundedCorners getRadius4dp() {
        if (null == mRadius4dp)
            mRadius4dp = new RoundedCorners(DimensionsUtil.getRoundingRadiusPixelSize4dp());
        return mRadius4dp;
    }

    public static RoundedCorners getRadius8dp() {
        if (null == mRadius8dp)
            mRadius8dp = new RoundedCorners(DimensionsUtil.getRoundingRadiusPixelSize8dp());
        return mRadius8dp;
    }

    public static RoundedCorners getRadius16dp() {
        if (null == mRadius16dp)
            mRadius16dp = new RoundedCorners(DimensionsUtil.getRoundingRadiusPixelSize16dp());
        return mRadius16dp;
    }

    public static CircleCrop getCircleCrop() {
        if (null == mCircleCrop) mCircleCrop = new CircleCrop();
        return mCircleCrop;
    }

    public static CenterCrop getCenterCrop() {
        if (null == mCenterCrop)
            mCenterCrop = new CenterCrop();
        return mCenterCrop;
    }
}
