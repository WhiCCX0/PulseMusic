package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.PlaylistDataAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleGestureCallback;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;
import java.util.List;

public class CurrentPlaylistActivity extends MediaSessionActivity implements PlaylistItemListener, SimpleGestureCallback {

    private TrackManager tm;
    private List<MusicModel> mCurrentList;
    private ItemTouchHelper itemTouchHelper;
    private PlaylistDataAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_data);

        tm = TrackManager.getInstance();

        Toolbar t = findViewById(R.id.toolbar);
        t.setTitle(R.string.playlist_current_queue);
        t.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.open_track_picker_btn).setOnClickListener(v ->
                startActivityForResult(new Intent(this, TrackPickerActivity.class), TrackPickerActivity.REQUEST_CODE));

        loadPlaylist(tm.getActiveQueue());
    }

    private void loadPlaylist(List<MusicModel> list) {
        if (null != list && list.size() > 0) {
            findViewById(R.id.no_tracks_found_tv).setVisibility(View.GONE);
            mCurrentList = new ArrayList<>(list);
            RecyclerView recyclerView = findViewById(R.id.playlist_data_rv);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mAdapter = new PlaylistDataAdapter(mCurrentList, getLayoutInflater(), this, this);
            recyclerView.setAdapter(mAdapter);

            ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
            itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            recyclerView.scrollToPosition(TrackManager.getInstance().getActiveIndex());
        } else findViewById(R.id.no_tracks_found_tv).setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        tm.buildDataList(mCurrentList, position);
        playMedia();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
    }

    @Override
    public void onItemDismissed(int position) {
        if (tm.canRemoveItem(position)) {
            tm.removeItemFromActiveQueue(position);

            Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
            sb.setAction(getString(R.string.snack_bar_action_undo), v -> {
                mAdapter.restoreItem();
                tm.restoreItem(position, mCurrentList.get(position));
            });
            sb.show();
        } else
            Toast.makeText(this, getString(R.string.cannot_remove_active_item), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        tm.updateActiveQueue(fromPosition, toPosition);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TrackPickerActivity.REQUEST_CODE) {
            if (null != data && null != data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS)) {
                ArrayList<MusicModel> selectedTracks = (ArrayList<MusicModel>) data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS);
                if (null != selectedTracks && selectedTracks.size() > 0) {
                    if (null == mAdapter) loadPlaylist(selectedTracks);
                    else {
                        mCurrentList.addAll(selectedTracks);
                        mAdapter.addItems(selectedTracks);
                    }
                }
            }
        }
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
