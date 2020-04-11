package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

import java.util.Hashtable;

public class MediaArtHelper {

    private static Hashtable<Long, Integer> table = new Hashtable<>();
    private static final Handler handler = new Handler(Looper.getMainLooper());

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

    public static Bitmap getMediaArtBitmap(Context context, long albumId, RoundingRadius r) {
        return MediaArtGenerator.generateMediaArtBitmap(context, getValue(albumId), r);
    }

    public static void getMediaArtBitmapAsync(Context context, long albumId, RoundingRadius r, TaskRunner.Callback<Bitmap> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            Bitmap bitmap = MediaArtGenerator.generateMediaArtBitmap(context, getValue(albumId), r);
            handler.post(() -> callback.onComplete(bitmap));
        });
    }

    public static void getMediaArtDrawableAsync(Context context, long albumId, RoundingRadius r, TaskRunner.Callback<Drawable> callback) {
        TaskRunner.getInstance().executeAsync(() -> {
            Drawable drawable = MediaArtGenerator.generateMediaArtDrawable(context, getValue(albumId), r);
            handler.post(() -> callback.onComplete(drawable));
        });
    }

    public enum RoundingRadius {
        RADIUS_2dp,
        RADIUS_4dp,
        RADIUS_8dp,
        RADIUS_16dp
    }

    private static class MediaArtGenerator {

        private static Bitmap mBitmap;
        private static Canvas mCanvas;
        private static TypedArray mMediaArtColors;

        private static int getRoundingRadius(RoundingRadius r) {
            switch (r) {
                case RADIUS_2dp:
                    return DimensionsUtil.getRoundingRadiusPixelSize2dp();
                case RADIUS_4dp:
                    return DimensionsUtil.getRoundingRadiusPixelSize4dp();
                case RADIUS_8dp:
                    return DimensionsUtil.getRoundingRadiusPixelSize8dp();
                case RADIUS_16dp:
                default:
                    return DimensionsUtil.getRoundingRadiusPixelSize16dp();
            }
        }

        @NonNull
        static Drawable generateMediaArtDrawable(Context context, int index, RoundingRadius r) {
            if (null == mMediaArtColors)
                mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
            //To match image view background with 100px x 100 px and 18 rounding radius
            //Drawable with 24dp x 24dp with 4 dp rounding radius is required
            //which scales proportionally : 96dp x 96dp with 16 dp rounding radius
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.RECTANGLE);
            background.setCornerRadius(getRoundingRadius(r));
            background.setColor(mMediaArtColors.getColor(index, 0));
            Log.e("MediaArtGenerator", "Current thread = "+Thread.currentThread().getName());
            return new LayerDrawable(new Drawable[]{background, context.getDrawable(R.drawable.ic_media_error_art)});
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
}
