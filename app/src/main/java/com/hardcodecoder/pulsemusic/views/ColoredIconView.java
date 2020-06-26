package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class ColoredIconView extends AppCompatImageView {

    public ColoredIconView(Context context) {
        super(context);
        initialize(context, null);
    }

    public ColoredIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ColoredIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, @Nullable AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ColoredIconView);
        setBackground(context.getDrawable(R.drawable.plain_circle));
        int paddingPixels = context.getResources().getDimensionPixelSize(R.dimen.icon_padding);
        setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
        setImageResource(typedArray.getResourceId(R.styleable.ColoredIconView_icon, R.drawable.def_colored_icon_view_icon));

        int iconColor = typedArray.getColor(R.styleable.ColoredIconView_iconColor, Color.BLUE);
        int iconBackgroundColor = typedArray.getColor(R.styleable.ColoredIconView_iconBackgroundColor, iconColor);

        boolean desaturated = ThemeManagerUtils.isDarkModeEnabled() && AppSettings.getAccentDesaturatedColor(context)
                && typedArray.getBoolean(R.styleable.ColoredIconView_desaturatedColorInDarkMode, true);

        if (desaturated) {
            iconColor = context.getResources().getColor(R.color.darkColorBackground);
            iconBackgroundColor = ColorUtil.mixColors(iconBackgroundColor, Color.WHITE, 0.4f);
        } else
            iconBackgroundColor = ColorUtil.changeColorAlphaTo20(iconBackgroundColor);

        setImageTintList(ColorStateList.valueOf(iconColor));
        setBackgroundTintList(ColorStateList.valueOf(iconBackgroundColor));

        typedArray.recycle();
    }
}
