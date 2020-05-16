package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.TrackPickerAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewSelectorHelper;
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class TrackPickerActivity extends PMBActivity implements TrackPickerListener {

    public static final String ID_PICKED_TRACKS = "picked_tracks";
    public static final int REQUEST_CODE = 100;
    private RecyclerViewSelectorHelper mSelectorHelper;
    private ArrayList<MusicModel> mSelectedTracks = new ArrayList<>();
    private List<MusicModel> mMainList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_picker);

        findViewById(R.id.btn_done).setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra(ID_PICKED_TRACKS, mSelectedTracks);
            setResult(RESULT_OK, i);
            finishActivity(REQUEST_CODE);
            finish();
            overrideActivityTransition();
        });

        MaterialToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(getString(R.string.select_tracks));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overrideActivityTransition();
        });

        mMainList = new ArrayList<>(LoaderCache.getAllTracksList());
        RecyclerView recyclerView = findViewById(R.id.track_picker_rv);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.item_falls_down_animation);
        recyclerView.setLayoutAnimation(controller);
        TrackPickerAdapter adapter = new TrackPickerAdapter(mMainList, getLayoutInflater(), this);
        recyclerView.setAdapter(adapter);
        mSelectorHelper = new RecyclerViewSelectorHelper(adapter);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, int position, boolean isSelected) {
        if (isSelected) {
            mSelectedTracks.remove(mMainList.get(position));
            mSelectorHelper.onItemUnSelected(viewHolder, position);
        } else {
            mSelectedTracks.add(mMainList.get(position));
            mSelectorHelper.onItemSelected(viewHolder, position);
        }
    }

    private void overrideActivityTransition() {
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideActivityTransition();
    }
}
