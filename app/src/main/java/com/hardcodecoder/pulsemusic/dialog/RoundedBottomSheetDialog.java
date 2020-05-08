package com.hardcodecoder.pulsemusic.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hardcodecoder.pulsemusic.R;

public class RoundedBottomSheetDialog extends BottomSheetDialog {

    public RoundedBottomSheetDialog(@NonNull Context context) {
        super(context, R.style.BaseBottomSheetDialog);
    }
}
