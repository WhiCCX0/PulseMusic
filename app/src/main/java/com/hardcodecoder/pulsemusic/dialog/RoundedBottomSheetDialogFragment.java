package com.hardcodecoder.pulsemusic.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hardcodecoder.pulsemusic.R;

import java.util.Objects;

public class RoundedBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(Objects.requireNonNull(getContext()), getTheme());
    }
}
