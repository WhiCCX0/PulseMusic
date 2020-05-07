package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;

public class SettingsToggleableItem extends FrameLayout {

    public SettingsToggleableItem(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SettingsToggleableItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, @Nullable AttributeSet attributeSet) {
        View view = View.inflate(context, R.layout.settings_toggleable_item_layout, this);
        MaterialTextView title = view.findViewById(R.id.setting_toggleable_item_title);
        MaterialTextView text = view.findViewById(R.id.setting_toggleable_item_text);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SettingsToggleableItem);
        title.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemTitle));
        text.setText(typedArray.getText(R.styleable.SettingsToggleableItem_settingToggleableItemText));

        typedArray.recycle();
    }
}
