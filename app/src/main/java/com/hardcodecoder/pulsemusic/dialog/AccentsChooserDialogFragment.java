package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.adapters.AccentAdapter;
import com.hardcodecoder.pulsemusic.interfaces.SettingsFragmentsListener;
import com.hardcodecoder.pulsemusic.model.AccentsModel;
import com.hardcodecoder.pulsemusic.themes.ThemeManagerUtils;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.ArrayList;
import java.util.List;

public class AccentsChooserDialogFragment extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "AccentsChooser";
    private int newAccentId;

    public static AccentsChooserDialogFragment getInstance() {
        return new AccentsChooserDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_accents_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int[] mIds = view.getContext().getResources().getIntArray(R.array.pulse_accent_colors_id);
        String[] mTitles = view.getContext().getResources().getStringArray(R.array.pulse_accent_colors_title);
        int[] mColors;

        if (ThemeManagerUtils.isDarkModeEnabled() && ThemeManagerUtils.isAccentsDesaturated())
            mColors = view.getContext().getResources().getIntArray(R.array.pulse_accent_colors_value_desaturated);
        else
            mColors = view.getContext().getResources().getIntArray(R.array.pulse_accent_colors_value);

        final SettingsFragmentsListener mListener = (SettingsFragmentsListener) getActivity();

        List<AccentsModel> mList = new ArrayList<>(mColors.length);
        for (int i = 0; i < mColors.length; i++) {
            mList.add(new AccentsModel(
                    mIds[i],
                    mTitles[i] == null ? getString(R.string.laf_accents_picker_accent_title_unknown) : mTitles[i],
                    mColors[i]));
        }
        int currentId = AppSettings.getSelectedAccentId(view.getContext());

        if (mList.size() > 0) {
            RecyclerView recyclerView = view.findViewById(R.id.accents_display_rv);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
            AccentAdapter adapter = new AccentAdapter(mList, getLayoutInflater(), currentId, position -> {
                newAccentId = mList.get(position).getId();
                dismiss();
                if (ThemeManagerUtils.setSelectedAccentColor(view.getContext(), newAccentId))
                    if (mListener instanceof SettingsActivity) {
                        mListener.onThemeChanged();
                    }
            });
            recyclerView.setAdapter(adapter);
        }
        view.findViewById(R.id.choose_accents_cancel_btn).setOnClickListener(v -> dismiss());
    }

}
