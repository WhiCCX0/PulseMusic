package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.TrackPickerAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;

public class TrackPickerActivity extends PMBActivity implements ItemClickListener.Selector {

    public static final String ID_PICKED_TRACKS = "picked_tracks";
    public static final int REQUEST_CODE = 100;
    private ArrayList<MusicModel> mSelectedTracks = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_picker);

        findViewById(R.id.btn_done).setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra(ID_PICKED_TRACKS, mSelectedTracks);
            setResult(RESULT_OK, i);
            finish();
            overrideActivityTransition();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.select_tracks));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overrideActivityTransition();
        });

        RecyclerView recyclerView = findViewById(R.id.track_picker_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.item_falls_down_animation);
        recyclerView.setLayoutAnimation(controller);
        TrackPickerAdapter adapter = new TrackPickerAdapter(TrackManager.getInstance().getMainList(), getLayoutInflater(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSelected(MusicModel md) {
        mSelectedTracks.add(md);
    }

    @Override
    public void onUnselected(MusicModel md) {
        mSelectedTracks.remove(md);
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
