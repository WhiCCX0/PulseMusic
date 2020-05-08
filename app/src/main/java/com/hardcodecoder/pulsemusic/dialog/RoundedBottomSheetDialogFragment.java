package com.hardcodecoder.pulsemusic.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class RoundedBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new RoundedBottomSheetDialog(Objects.requireNonNull(getContext()));
    }
}