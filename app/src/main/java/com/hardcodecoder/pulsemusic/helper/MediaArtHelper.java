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

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

import java.util.Hashtable;

public class MediaArtHelper {

    private static final Handler handler = new Handler(Looper.getMainLooper());
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

    public static Bitmap getMediaArtBitmap(Context context, long albumId, RoundingRadius r) {
        return MediaArtGenerator.generateMediaArtBitmap(context, getValue(albumId), r);
    }

    public static Drawable getTintedGradientDrawable(Context context) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                new int[]{ColorUtil.generatePrimaryTintedColorOverlay(context), android.R.color.transparent});
        drawable.setCornerRadius(DimensionsUtil.getRoundingRadiusPixelSize16dp());
        return drawable;
    }

    public static void getMediaArtBitmapAsync(Context context, long albumId, RoundingRadius r, TaskRunner.Callback<Bitmap> callback) {
        TaskRunner.executeAsync(() -> {
            Bitmap bitmap = MediaArtGenerator.generateMediaArtBitmap(context, getValue(albumId), r);
            handler.post(() -> callback.onComplete(bitmap));
        });
    }

    public static void getMediaArtDrawableAsync(Context context, int[] imageViewWidthHeight, long albumId, RoundingRadius r, TaskRunner.Callback<Drawable> callback) {
        TaskRunner.executeAsync(() -> {
            Drawable drawable = MediaArtGenerator.generateMediaArtDrawable(context, imageViewWidthHeight, getValue(albumId), r);
            handler.post(() -> callback.onComplete(drawable));
        });
    }

    public enum RoundingRadius {
        RADIUS_NONE,
        RADIUS_2dp,
        RADIUS_4dp,
        RADIUS_8dp,
        RADIUS_16dp,
        CIRCLE
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
                    return DimensionsUtil.getRoundingRadiusPixelSize16dp();
                case RADIUS_NONE:
                default:
                    return 0;
            }
        }

        @NonNull
        static Drawable generateMediaArtDrawable(Context context, int[] dimensions, int index, RoundingRadius r) {
            if (null == mMediaArtColors)
                mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
            //To match image view background with 100px x 100 px and 18 rounding radius
            //Drawable with 24dp x 24dp with 4 dp rounding radius is required
            //which scales proportionally : 96dp x 96dp with 16 dp rounding radius
            GradientDrawable background = new GradientDrawable();
            background.setSize(dimensions[0], dimensions[1]);
            background.setShape(GradientDrawable.RECTANGLE);
            int radius = r == RoundingRadius.CIRCLE ?
                    (int) Math.ceil((float) dimensions[0] / 2) :
                    getRoundingRadius(r);
            background.setCornerRadius(radius);
            background.setColor(mMediaArtColors.getColor(index, 0));
            return new LayerDrawable(new Drawable[]{background, context.getDrawable(R.drawable.ic_media_error_art)});
        }

        @NonNull
        static Bitmap generateMediaArtBitmap(Context context, int index, RoundingRadius r) {
            int sides = context.getResources().getDimensionPixelSize(R.dimen.now_playing_media_art_iv);
            Drawable d = generateMediaArtDrawable(context, new int[]{sides, sides}, index, r);
            if (null == mBitmap) {
                mBitmap = Bitmap.createBitmap(sides, sides, Bitmap.Config.ARGB_8888);
            }
            if (null == mCanvas)
                mCanvas = new Canvas(mBitmap);
            d.setBounds(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
            d.draw(mCanvas);
            return mBitmap;
        }
    }
}
