package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.PlaylistDataAdapter;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ClickDragRvListener;
import com.hardcodecoder.pulsemusic.interfaces.RecyclerViewGestures;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.DataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistDetailsActivity extends MediaSessionActivity implements ClickDragRvListener, RecyclerViewGestures.GestureCallback {

    public static final String KEY_TITLE = "playlist name";
    private Handler mHandler = new Handler();
    private PlaylistDataAdapter mAdapter;
    private ItemTouchHelper itemTouchHelper;
    private TrackManager tm;
    private List<MusicModel> mPlaylistTracks = new ArrayList<>();
    private String playListTitle = "";
    private boolean isPlaylistModified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_data);

        tm = TrackManager.getInstance();

        if (getIntent().getExtras() != null)
            playListTitle = getIntent().getExtras().getString(KEY_TITLE);

        DataManager.getPlaylistTracksAsync(this, playListTitle, result -> TaskRunner.executeAsync(() -> {
            if (null != result && result.size() > 0) {
                List<MusicModel> modelList = DataModelHelper.getModelFromTitle(result);
                mPlaylistTracks.addAll(modelList);
                mHandler.post(() -> {
                    RecyclerView recyclerView = findViewById(R.id.playlist_data_rv);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter = new PlaylistDataAdapter(mPlaylistTracks, getLayoutInflater(), this);
                    recyclerView.setAdapter(mAdapter);

                    ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(this);
                    itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                });
            } else
                mHandler.post(() -> findViewById(R.id.no_tracks_found_tv).setVisibility(View.VISIBLE));
        }));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(playListTitle);
        toolbar.setNavigationOnClickListener(v -> finish());

        FloatingActionButton fab = findViewById(R.id.open_track_picker_btn);
        fab.setOnClickListener(v -> startActivityForResult(new Intent(this, TrackPickerActivity.class), TrackPickerActivity.REQUEST_CODE));
    }


    @Override
    public void onItemClick(int position) {
        tm.buildDataList(mPlaylistTracks, position);
        playMedia();
    }

    @Override
    public void initiateDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
        holder.itemView.setBackground(getDrawable(R.drawable.active_item_background));
    }

    @Override
    public void onItemSwiped(int itemAdapterPosition) {
        final MusicModel deletedItem = mPlaylistTracks.remove(itemAdapterPosition);
        final int deletedIndex = itemAdapterPosition;
        mAdapter.removeItem(deletedIndex);
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction("UNDO", v -> {
            mPlaylistTracks.add(deletedIndex, deletedItem);
            mAdapter.restoreItem(deletedItem, deletedIndex);
        });
        isPlaylistModified = true;
        sb.show();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mPlaylistTracks, fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemMoved(int fromPos, int toPos) {
        isPlaylistModified = true;
    }

    @Override
    public void onClearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackground(getDrawable(android.R.color.transparent));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TrackPickerActivity.REQUEST_CODE) {
            if (null != data && null != data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS)) {
                TaskRunner.executeAsync(() -> {
                    ArrayList<String> list = (ArrayList<String>) data.getSerializableExtra(TrackPickerActivity.ID_PICKED_TRACKS);
                    if (null != list) {
                        List<MusicModel> modelList = DataModelHelper.getModelFromTitle(list);
                        mPlaylistTracks.addAll(modelList);
                        mHandler.post(() -> mAdapter.addItems(modelList));
                        DataManager.savePlaylistTracks(this, playListTitle, list);
                    }
                });
            }
        }
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
