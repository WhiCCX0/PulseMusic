package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

import java.util.Hashtable;

public class MediaArtHelper {

    private static Hashtable<Long, Integer> table = new Hashtable<>();

    private static Integer generateValue(long albumId) {
        int index = (int) albumId % 10;
        table.put(albumId, index);
        return index;
    }

    private static Integer getValue(long albumId) {
        if (table.containsKey(albumId))
            return table.get(albumId);
        return generateValue(albumId);
    }

    public static Drawable getMediaArtDrawable(Context context, long albumId, RoundingRadius r) {
        return MediaArtGenerator.generateMediaArtDrawable(context, getValue(albumId), r);
    }

    public static Bitmap getMediaArtBitmap(Context context, long albumId, RoundingRadius r) {
        return MediaArtGenerator.generateMediaArtBitmap(context, getValue(albumId), r);
    }

    private static class MediaArtGenerator {

        private static Bitmap mBitmap;
        private static Canvas mCanvas;
        private static TypedArray mMediaArtColors;

        private static int getRoundingRadius(RoundingRadius r){
            switch(r){
                case RADIUS_2dp:
                    return DimensionsUtil.getRoundingRadiusPixelSize2dp();
                case RADIUS_4dp:
                    return DimensionsUtil.getRoundingRadiusPixelSize4dp();
                case RADIUS_8dp:
                    return DimensionsUtil.getRoundingRadiusPixelSize8dp();
                case RADIUS_18dp:
                default:
                    return DimensionsUtil.getRoundingRadiusPixelSize18dp();
            }
        }

        @NonNull
        static Drawable generateMediaArtDrawable(Context context, int index, RoundingRadius r) {
            if (null == mMediaArtColors)
                mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
            GradientDrawable background = new GradientDrawable();
            Drawable icon = context.getDrawable(R.drawable.ic_media_error_art);
            background.setShape(GradientDrawable.RECTANGLE);
            background.setCornerRadius(getRoundingRadius(r));
            background.setColor(mMediaArtColors.getColor(index, 0));
            return new LayerDrawable(new Drawable[]{background, icon});
        }

        @NonNull
        static Bitmap generateMediaArtBitmap(Context context, int index, RoundingRadius r) {
            Drawable d = generateMediaArtDrawable(context, index, r);
            if (null == mBitmap)
                mBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            if (null == mCanvas)
                mCanvas = new Canvas(mBitmap);
            d.setBounds(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
            d.draw(mCanvas);
            return mBitmap;
        }

    }
    public enum RoundingRadius{
        RADIUS_2dp,
        RADIUS_4dp,
        RADIUS_8dp,
        RADIUS_18dp
    }
}
