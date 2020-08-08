package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ColorUtil;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class SettingsCategoryItemView extends FrameLayout {

    public SettingsCategoryItemView(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public SettingsCategoryItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SettingsCategoryItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }


    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
        View view = View.inflate(context, R.layout.settings_category_list_item, this);
        MaterialTextView title = view.findViewById(R.id.settings_list_item_title);
        MaterialTextView text = view.findViewById(R.id.settings_list_item_text);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingsCategoryItemView);

        if (title.getTypeface() != null && typedArray.hasValue(R.styleable.SettingsCategoryItemView_android_textStyle)) {
            title.setTypeface(title.getTypeface(), typedArray.getInteger(R.styleable.SettingsCategoryItemView_android_textStyle, Typeface.NORMAL));
        }

        if (typedArray.hasValue(R.styleable.SettingsCategoryItemView_settingItemIcon)) {
            ImageView icon = view.findViewById(R.id.settings_list_item_icon);
            icon.setImageDrawable(typedArray.getDrawable(R.styleable.SettingsCategoryItemView_settingItemIcon));

            int iconColor = typedArray.getColor(R.styleable.SettingsCategoryItemView_settingItemIconColor, Color.BLUE);
            int iconBackgroundColor = typedArray.getColor(R.styleable.SettingsCategoryItemView_settingItemIconBackgroundColor, iconColor);

            boolean desaturated = ThemeManagerUtils.isDarkModeEnabled() && AppSettings.getAccentDesaturatedColor(context)
                    && typedArray.getBoolean(R.styleable.SettingsCategoryItemView_settingItemDesaturatedColorsInDarkMode, true);

            if (desaturated) {
                iconColor = context.getResources().getColor(R.color.darkColorBackground);
                iconBackgroundColor = ColorUtil.mixColors(iconBackgroundColor, Color.WHITE, 0.4f);
            } else
                iconBackgroundColor = ColorUtil.changeColorAlphaTo20(iconBackgroundColor);

            icon.setBackgroundTintList(ColorStateList.valueOf(iconBackgroundColor));
            icon.setImageTintList(ColorStateList.valueOf(iconColor));
        }

        title.setText(typedArray.getText(R.styleable.SettingsCategoryItemView_settingItemTitle));
        text.setText(typedArray.getText(R.styleable.SettingsCategoryItemView_settingItemText));

        typedArray.recycle();
    }

}
