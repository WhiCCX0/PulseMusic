package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil.RoundingRadius;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

public class MediaArtHelper {

    public static Bitmap loadDynamicAlbumArtBitmap(Context context, long albumId) {
        final int[] dimensions = {512, 512};
        Drawable drawable = ImageUtil.generateDynamicAlbumArt(
                context,
                dimensions,
                getValue(albumId),
                DimensionsUtil.getRoundingRadius(RoundingRadius.RADIUS_NONE));
        return ImageUtil.createBitmapFrom(drawable, dimensions);
    }

    public static void setDynamicAlbumArtOnLoadFailed(@NonNull ImageView imageView, long albumId, RoundingRadius roundingRadius) {
        TaskRunner.executeAsync(() -> {
            Drawable drawable = ImageUtil.generateDynamicAlbumArt(
                    imageView.getContext(),
                    new int[]{imageView.getWidth(), imageView.getHeight()},
                    getValue(albumId),
                    DimensionsUtil.getRoundingRadius(roundingRadius));
            imageView.post(() -> imageView.setImageDrawable(drawable));
        });
    }

    public static void loadDynamicAlbumArt(@NonNull ImageView imageView, long albumId, RoundingRadius roundingRadius, Callback<Drawable> callback) {
        TaskRunner.executeAsync(() -> {
            Drawable drawable = ImageUtil.generateDynamicAlbumArt(imageView.getContext(),
                    new int[]{imageView.getWidth(), imageView.getHeight()},
                    getValue(albumId),
                    DimensionsUtil.getRoundingRadius(roundingRadius));
            callback.onComplete(drawable);
        });
    }

    private static Integer getValue(long albumId) {
        return Math.abs((int) albumId % 10);
    }
}
