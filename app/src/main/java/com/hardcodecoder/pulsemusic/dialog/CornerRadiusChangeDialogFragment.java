package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.ValueSlider;

public class CornerRadiusChangeDialogFragment extends RoundedBottomSheetDialogFragment {

    public static final String TAG = CornerRadiusChangeDialogFragment.class.getSimpleName();

    public static CornerRadiusChangeDialogFragment getInstance() {
        return new CornerRadiusChangeDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_corner_radius_changer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ValueSlider topLeft = view.findViewById(R.id.radius_slider_tl);
        ValueSlider topRight = view.findViewById(R.id.radius_slider_tr);
        ValueSlider bottomLeft = view.findViewById(R.id.radius_slider_bl);
        ValueSlider bottomRight = view.findViewById(R.id.radius_slider_br);

        int[] radiusValues = AppSettings.getNowPlayingAlbumCoverCornerRadius(view.getContext());

        topLeft.setSliderValue(radiusValues[0]);
        topRight.setSliderValue(radiusValues[1]);
        bottomLeft.setSliderValue(radiusValues[2]);
        bottomRight.setSliderValue(radiusValues[3]);

        view.findViewById(R.id.radius_changer_set_btn).setOnClickListener(v -> {
            AppSettings.saveNowPlayingAlbumCoverCornerRadius(
                    view.getContext(),
                    topLeft.getSliderValue(),
                    topRight.getSliderValue(),
                    bottomLeft.getSliderValue(),
                    bottomRight.getSliderValue());
            dismiss();
        });
    }
}
