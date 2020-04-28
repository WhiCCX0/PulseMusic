package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.PlaylistDataAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistItemListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleGestureCallback;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailsActivity extends MediaSessionActivity implements PlaylistItemListener, SimpleGestureCallback {

    public static final String KEY_TITLE = "playlist name";
    private PlaylistDataAdapter mAdapter;
    private ItemTouchHelper itemTouchHelper;
    private TrackManager tm;
    private List<MusicModel> mPlaylistTracks;
    private String playListTitle = "";
    private boolean isPlaylistModified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_data);

        tm = TrackManager.getInstance();

        if (getIntent().getExtras() != null)
            playListTitle = getIntent().getExtras().getString(KEY_TITLE);

        StorageHelper.getPlaylistTracks(this, playListTitle, this::loadPlaylist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(playListTitle);
        toolbar.setNavigationOnClickListener(v -> finish());

        FloatingActionButton fab = findViewById(R.id.open_track_picker_btn);
        fab.setOnClickListener(v -> startActivityForResult(new Intent(this, TrackPickerActivity.class), TrackPickerActivity.REQUEST_CODE));
    }

    private void loadPlaylist(List<MusicModel> list) {
        if (null != list && list.size() > 0) {
            mPlaylistTracks = new ArrayList<>(list);
            RecyclerView recyclerView = findViewById(R.id.playlist_data_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new PlaylistDataAdapter(mPlaylistTracks, getLayoutInflater(), this, this);
            recyclerView.setAdapter(mAdapter);

            ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
            itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        } else findViewById(R.id.no_tracks_found_tv).setVisibility(View.VISIBLE);
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
        Log.e("PlaylistDetailsActivity", "onItemSwiped position = " + position);
        //final MusicModel deletedItem = mPlaylistTracks.remove(itemAdapterPosition);
        //final int deletedIndex = itemAdapterPosition;
        //mAdapter.removeItem(position);
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction("UNDO", v -> {
            Log.e("PlaylistDetailsActivity", "onItemSwiped restored position = " + position);
            //mPlaylistTracks.add(deletedIndex, deletedItem);
            mAdapter.restoreItem();
        });
        isPlaylistModified = true;
        sb.show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        isPlaylistModified = true;
    }

    /*@Override
    public void initiateDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
        holder.itemView.setBackground(getDrawable(R.drawable.active_item_background));
    }*/

    /*@Override
    public void onItemSwiped(int itemAdapterPosition) {
        Log.e("PlaylistDetailsActivity", "onItemSwiped position = "+itemAdapterPosition);
        //final MusicModel deletedItem = mPlaylistTracks.remove(itemAdapterPosition);
        //final int deletedIndex = itemAdapterPosition;
        mAdapter.removeItem(itemAdapterPosition);
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction("UNDO", v -> {
            Log.e("PlaylistDetailsActivity", "onItemSwiped restored position = "+itemAdapterPosition);
            //mPlaylistTracks.add(deletedIndex, deletedItem);
            mAdapter.restoreItem();
        });
        isPlaylistModified = true;
        sb.show();

    }*/

    /*@Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mPlaylistTracks, fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }*/

    /*@Override
    public void onItemMoved(int fromPos, int toPos) {
        isPlaylistModified = true;
    }*/

    /*@Override
    public void onClearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackground(getDrawable(android.R.color.transparent));
    }*/

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TrackPickerActivity.REQUEST_CODE) {
            if (null != data && null != data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS)) {
                ArrayList<MusicModel> selectedTracks = (ArrayList<MusicModel>) data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS);
                if (null != selectedTracks && selectedTracks.size() > 0) {
                    StorageHelper.addTracksToPlaylist(this, playListTitle, selectedTracks, result -> {
                        if (result) {
                            if (null == mAdapter) loadPlaylist(selectedTracks);
                            else {
                                mPlaylistTracks.addAll(selectedTracks);
                                mAdapter.addItems(selectedTracks);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isPlaylistModified)
            StorageHelper.updatePlaylistTracks(this, playListTitle, mPlaylistTracks);
        super.onDestroy();
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
