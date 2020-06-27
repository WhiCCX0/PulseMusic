package com.hardcodecoder.pulsemusic.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.ColorInt;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;

public class ImageUtil {

    private static TypedArray mMediaArtColors;

    public static Drawable generateDynamicAlbumArt(Context context, int[] dimension, int index, int radius) {
        LayerDrawable layerDrawable = (LayerDrawable) context.getDrawable(R.drawable.media_art_error);
        if (null != layerDrawable) {
            if (radius > 0) {
                GradientDrawable background = (GradientDrawable) layerDrawable.getDrawable(0);
                background.mutate();
                background.setSize(dimension[0], dimension[1]);
                background.setCornerRadius(radius);
            }
            Drawable foreground = layerDrawable.getDrawable(1);
            foreground.mutate();
            foreground.setTint(getColorFromAlbumArtColors(context, index));
        }
        return layerDrawable;
    }

    public static Bitmap createBitmapFrom(Drawable drawable, int[] dimension) {
        drawable.mutate();
        Bitmap bitmap = Bitmap.createBitmap(dimension[0], dimension[1], Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable getTintedGradientOverlay(Context context) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                new int[]{ColorUtil.generatePrimaryTintedColorOverlay(context), android.R.color.transparent});
        drawable.setCornerRadius(DimensionsUtil.getRoundingRadiusPixelSize16dp());
        return drawable;
    }

    @ColorInt
    private static int getColorFromAlbumArtColors(Context context, int index) {
        if (null == mMediaArtColors)
            mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
        return mMediaArtColors.getColor(index, 0);
    }
}
