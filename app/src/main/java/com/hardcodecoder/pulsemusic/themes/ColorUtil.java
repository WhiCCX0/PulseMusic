package com.hardcodecoder.pulsemusic.themes;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

import com.hardcodecoder.pulsemusic.R;

public class ColorUtil {

    @ColorInt
    public static int mixColors(@ColorInt int color1, @ColorInt int color2, float ratio) {
        float inverseRatio = 1f - ratio;
        float r = Color.red(color1) * ratio + Color.red(color2) * inverseRatio;
        float g = Color.green(color1) * ratio + Color.green(color2) * inverseRatio;
        float b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio;
        return Color.rgb((int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int changeColorAlphaTo20(@ColorInt int color1) {
         /*First byte in the color int is responsible for the transparency: 0 - completely transparent, 255 (0xFF) – opaque.
           In the first part ("&" operation) we set the first byte to 0 and left other bytes untouched.
           In the second part we set the first byte to 0x33 which is the 20% of 0xFF (255 * 0.2 ≈ 51).
         */
        return (color1 & 0x00FFFFFF) | 0x33000000;
    }

    @ColorInt
    public static int generatePrimaryTintedColorOverlay(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.primaryColor, typedValue, true);
        int color = typedValue.data;
         /*First byte in the color int is responsible for the transparency: 0 - completely transparent, 255 (0xFF) – opaque.
           In the first part ("&" operation) we set the first byte to 0 and left other bytes untouched.
           In the second part we set the first byte to 0x0D which is the 5% of 0xFF (255 * 0.05 ≈ 13).
         */
        return (color & 0x00FFFFFF) | 0x40000000;
    }
}
