package com.hardcodecoder.pulsemusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;

import java.util.Locale;

public class ValueSlider extends FrameLayout {

    private Slider mSlider;
    private MaterialTextView mStartValueText;
    private String suffix;

    public ValueSlider(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public ValueSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ValueSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
        View view = View.inflate(context, R.layout.layout_value_slider, this);
        mStartValueText = view.findViewById(R.id.slider_start_value_text);
        MaterialTextView endText = view.findViewById(R.id.slider_end_value_text);
        mSlider = view.findViewById(R.id.value_slider);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ValueSlider);

        int startValue = typedArray.getInt(R.styleable.ValueSlider_startValue, 0);
        int endValue = typedArray.getInt(R.styleable.ValueSlider_endValue, 100);
        int stepSize = typedArray.getInt(R.styleable.ValueSlider_stepSize, -1);
        suffix = typedArray.getString(R.styleable.ValueSlider_unit);

        endText.setText(String.format(Locale.getDefault(), "%d%s", endValue, suffix));

        mSlider.setValueFrom(startValue);
        mSlider.setValueTo(endValue);

        if (typedArray.hasValue(R.styleable.ValueSlider_defaultValue)) {
            int defValue = typedArray.getInt(R.styleable.ValueSlider_defaultValue, 0);
            mSlider.setValue(defValue);
            updateStartText(defValue);
        }
        if (stepSize != -1)
            mSlider.setStepSize(stepSize);

        mSlider.setLabelFormatter(value -> Math.round(value) + suffix);
        mSlider.addOnChangeListener((slider, value, fromUser) -> updateStartText(Math.round(value)));
        typedArray.recycle();
    }

    public int getSliderValue() {
        return Math.round(mSlider.getValue());
    }

    public void setSliderValue(int value) {
        mSlider.setValue(value);
        updateStartText(value);
    }

    private void updateStartText(int value) {
        mStartValueText.setText(String.format(Locale.getDefault(), "%d%s", Math.round(value), suffix));
    }
}
