package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;

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

    public static Drawable getMediaArtDrawable(Context context, long albumId) {
        return MediaArtGenerator.generateMediaArtDrawable(context, getValue(albumId));
    }

    public static Bitmap getMediaArtBitmap(Context context, long albumId) {
        return MediaArtGenerator.generateMediaArtBitmap(context, getValue(albumId));
    }

    private static class MediaArtGenerator {

        private static Bitmap mBitmap;
        private static Canvas mCanvas;
        private static TypedArray mMediaArtColors;
        private static Drawable mIcon;

        @NonNull
        static Drawable generateMediaArtDrawable(Context context, int index) {
            if (null == mMediaArtColors)
                mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
            if (null == mIcon)
                mIcon = context.getDrawable(R.drawable.ic_media_error_art);
            Drawable background = context.getDrawable(R.drawable.bck_media_error_art);
            if (null == background)
                throw new IllegalArgumentException("Cannot find required drawables");
            background.setTint(mMediaArtColors.getColor(index, 0));
            return new LayerDrawable(new Drawable[]{background, mIcon});
        }

        @NonNull
        static Bitmap generateMediaArtBitmap(Context context, int index) {
            Drawable d = generateMediaArtDrawable(context, index);
            if (null == mBitmap)
                mBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            if (null == mCanvas)
                mCanvas = new Canvas(mBitmap);
            d.setBounds(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
            d.draw(mCanvas);
            return mBitmap;
        }
    }
}
