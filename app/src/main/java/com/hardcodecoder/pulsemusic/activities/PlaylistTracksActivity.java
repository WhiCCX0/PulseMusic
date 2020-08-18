package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.PlaylistDataAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleGestureCallback;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;

import java.util.ArrayList;
import java.util.List;

public class PlaylistTracksActivity extends MediaSessionActivity implements PlaylistItemListener, SimpleGestureCallback {

    public static final String KEY_TITLE = "playlist name";
    private final Handler mHandler = new Handler();
    private PlaylistDataAdapter mAdapter;
    private ItemTouchHelper itemTouchHelper;
    private TrackManager tm;
    private List<MusicModel> mPlaylistTracks;
    private String playListTitle = "";
    private boolean isPlaylistModified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks);

        tm = TrackManager.getInstance();

        if (getIntent().getExtras() != null)
            playListTitle = getIntent().getExtras().getString(KEY_TITLE);

        if (playListTitle != null)
            AppFileManager.getPlaylistTracks(playListTitle, this::loadPlaylist);

        MaterialToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(playListTitle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.open_track_picker_btn).setOnClickListener(v ->
                startActivityForResult(new Intent(this, TrackPickerActivity.class), TrackPickerActivity.REQUEST_CODE));
    }

    private void loadPlaylist(List<MusicModel> list) {
        mHandler.post(() -> {
            if (null != list && list.size() > 0) {
                findViewById(R.id.no_tracks_found).setVisibility(View.GONE);
                mPlaylistTracks = new ArrayList<>(list);
                RecyclerView recyclerView = (RecyclerView) ((ViewStub) findViewById(R.id.stub_playlist_tracks_rv)).inflate();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                mAdapter = new PlaylistDataAdapter(mPlaylistTracks, getLayoutInflater(), this, this);
                recyclerView.setAdapter(mAdapter);

                ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
                itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);
            } else {
                MaterialTextView textView = findViewById(R.id.no_tracks_found);
                String str = getString(R.string.no_playlist_tracks_found);
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(str);
                int len = str.length();
                stringBuilder.setSpan(new AbsoluteSizeSpan((int) (textView.getTextSize() * 3.0)), len - 1, len, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                textView.setText(stringBuilder);
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        tm.buildDataList(mPlaylistTracks, position);
        playMedia();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemDismissed(int position) {
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.snack_bar_action_undo), v -> mAdapter.restoreItem());
        isPlaylistModified = true;
        sb.show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        isPlaylistModified = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TrackPickerActivity.REQUEST_CODE) {
            Object object;
            if (null != data && null != (object = data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS))) {
                ArrayList<MusicModel> selectedTracks = (ArrayList<MusicModel>) object;
                if (selectedTracks.size() > 0) {
                    AppFileManager.addItemsToPlaylist(playListTitle, selectedTracks, result -> mHandler.post(() -> {
                        if (result) {
                            if (null == mAdapter) loadPlaylist(selectedTracks);
                            else {
                                mAdapter.addItems(selectedTracks);
                            }
                        }
                    }));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isPlaylistModified)
            AppFileManager.updatePlaylistItems(playListTitle, mPlaylistTracks);
        super.onDestroy();
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
