package com.hardcodecoder.pulsemusic.activities;

import android.graphics.Typeface;
import android.media.session.MediaController;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.dialog.RoundedBottomSheetDialog;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.LibraryItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class RecentActivity extends MediaSessionActivity implements LibraryItemClickListener {

    private List<MusicModel> mRecentTracks;
    private TrackManager tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks);

        tm = TrackManager.getInstance();
        StorageHelper.getSavedHistory(this, result -> {
            if (null != result && result.size() > 0) {
                mRecentTracks = new ArrayList<>(result);
                RecyclerView rv = findViewById(R.id.rv_playlist_tracks);
                rv.setVisibility(View.VISIBLE);
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(rv.getContext(), R.anim.item_slide_up_animation);
                rv.setLayoutAnimation(controller);
                LibraryAdapter adapter = new LibraryAdapter(mRecentTracks, getLayoutInflater(), this);
                rv.setAdapter(adapter);
            } else {
                MaterialTextView tv = findViewById(R.id.no_tracks_found_tv);
                tv.setVisibility(View.VISIBLE);
                String text = getString(R.string.no_recent_tracks);
                int len = text.length();
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), len - 1, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                stringBuilder.setSpan(new AbsoluteSizeSpan((int) (tv.getTextSize() * 3.0)), len - 1, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                tv.setText(stringBuilder);
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(getString(R.string.recent));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.open_track_picker_btn).setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int pos) {
        tm.buildDataList(mRecentTracks, pos);
        playMedia();
    }

    @Override
    public void onOptionsClick(int pos) {
        showMenuItems(mRecentTracks.get(pos));
    }

    private void showMenuItems(MusicModel md) {
        View view = View.inflate(this, R.layout.library_item_menu, null);
        BottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(view.getContext());

        view.findViewById(R.id.track_play_next)
                .setOnClickListener(v -> {
                    tm.playNext(md);
                    Toast.makeText(v.getContext(), getString(R.string.play_next_toast), Toast.LENGTH_SHORT).show();
                    if (bottomSheetDialog.isShowing())
                        bottomSheetDialog.dismiss();
                });

        view.findViewById(R.id.add_to_queue)
                .setOnClickListener(v -> {
                    tm.addToActiveQueue(md);
                    Toast.makeText(v.getContext(), getString(R.string.add_to_queue_toast), Toast.LENGTH_SHORT).show();
                    if (bottomSheetDialog.isShowing())
                        bottomSheetDialog.dismiss();
                });

        view.findViewById(R.id.song_info).setOnClickListener(v -> {
            UIHelper.buildSongInfoDialog(this, md);
            if (bottomSheetDialog.isShowing())
                bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
