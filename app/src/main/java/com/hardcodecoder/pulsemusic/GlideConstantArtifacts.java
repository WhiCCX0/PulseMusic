package com.hardcodecoder.pulsemusic;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

public class GlideConstantArtifacts {

    private static RoundedCorners rc;
    private static RoundedCorners smallRR;
    private static CircleCrop mCircleCrop;

    static void init() {
        rc = new RoundedCorners(DimensionsUtil.getRoundingRadiusPixelSize18dp());
        smallRR = new RoundedCorners(DimensionsUtil.getRoundingRadiusPixelSize8dp());
        mCircleCrop = new CircleCrop();
    }

    public static RoundedCorners getDefaultRoundingRadius() {
        return rc;
    }

    public static RoundedCorners getRoundingRadiusSmall () { return smallRR; }

    public static CircleCrop getCircleCrop () { return mCircleCrop; }

}
