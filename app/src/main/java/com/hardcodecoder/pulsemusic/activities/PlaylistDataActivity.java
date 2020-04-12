package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

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
import com.hardcodecoder.pulsemusic.adapters.PlaylistDataAdapter;
import com.hardcodecoder.pulsemusic.helper.DataManager;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.helper.TrackPickerHelper;
import com.hardcodecoder.pulsemusic.interfaces.ClickDragRvListener;
import com.hardcodecoder.pulsemusic.interfaces.RecyclerViewGestures;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistDataActivity extends MediaSessionActivity implements ClickDragRvListener, RecyclerViewGestures.GestureCallback {

    public static final String TITLE_KEY = "playlist name";
    public static final String ITEM_NUMBER_KEY = "playlist number";
    private boolean isCurrentQueue = false;
    private List<MusicModel> mList = new ArrayList<>();
    private PlaylistDataAdapter adapter;
    private boolean isPlaylistDataModified = false;
    private int mPlaylistCardIndex;
    private TrackManager tm;
    private ItemTouchHelper itemTouchHelper;
    private String playlistName;
    private boolean isFavPlaylist = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tm = TrackManager.getInstance();
        setContentView(R.layout.activity_playlist_data);
        if (getIntent().getExtras() != null) {
            playlistName = getIntent().getExtras().getString(TITLE_KEY);
            mPlaylistCardIndex = getIntent().getExtras().getInt(ITEM_NUMBER_KEY);
            if (mPlaylistCardIndex == 0) isCurrentQueue = true;
            else if (mPlaylistCardIndex == 1) isFavPlaylist = true;
            updateData();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(playlistName);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void updateData() {
        /*if (isCurrentQueue && null != tm.getActiveQueue())
            mList.addAll(tm.getActiveQueue());
        else if (isFavPlaylist)
            mList = PlaylistStorageManager.getFavorite(this);
        else if (!isCurrentQueue)
            mList = PlaylistStorageManager.getPlaylistTrackAtPosition(this, mPlaylistCardIndex - 2);

        FloatingActionButton fab = findViewById(R.id.open_track_picker_btn);
        fab.setOnClickListener(v -> startActivity(new Intent(this, TrackPickerActivity.class)));
        setRv();*/

        if (isCurrentQueue && null != tm.getActiveQueue()) {
            mList.addAll(tm.getActiveQueue());
            setRv(mList);
        } else {
            if (isFavPlaylist) {
                //mList = PlaylistStorageManager.getFavorite(this);
                DataManager.getSavedFavoriteTracksAsync(this, favorites -> {
                    mList = favorites;
                    setRv(mList);
                });
            } else if (!isCurrentQueue) {
                ///mList = PlaylistStorageManager.getPlaylistTrackAtPosition(this, mPlaylistCardIndex - 2);
                DataManager.getPlaylistDataAtAsync(this, mPlaylistCardIndex - 2, playlist -> {
                    mList = playlist;
                    setRv(mList);
                });
            }
        }
        FloatingActionButton fab = findViewById(R.id.open_track_picker_btn);
        fab.setOnClickListener(v -> startActivity(new Intent(this, TrackPickerActivity.class)));
    }

    private void setRv(List<MusicModel> list) {
        RecyclerView recyclerView = findViewById(R.id.playlist_data_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.item_falls_down_animation);
        recyclerView.setLayoutAnimation(controller);
        adapter = new PlaylistDataAdapter(list, this, getLayoutInflater());
        recyclerView.setAdapter(adapter);

        /*
         * Setting up the swipe gestures
         */
        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(this/*, mActivity*/);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if (isCurrentQueue)
            recyclerView.scrollToPosition(TrackManager.getInstance().getActiveIndex());
    }

    @Override
    public void onItemClick(int position) {
        tm.buildDataList(mList, position);
        playMedia();
    }

    @Override
    public void initiateDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
        holder.itemView.setBackground(getDrawable(R.drawable.active_item_background));
    }

    /**
     * New implementation
     *
     * @param itemAdapterPosition provides the index of the current item
     */
    @Override
    public void onItemSwiped(final int itemAdapterPosition) {
        if (isCurrentQueue) {
            if (itemAdapterPosition == tm.getActiveIndex()) {
                Toast.makeText(this, "Cannot remove active item", Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(itemAdapterPosition);
                return;
            } else {
                if (!tm.canRemoveItem(itemAdapterPosition)) {
                    Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        MusicModel del = mList.remove(itemAdapterPosition);
        isPlaylistDataModified = true;
        adapter.removeItem(itemAdapterPosition);
        if (isCurrentQueue) tm.removeItemFromActiveQueue(itemAdapterPosition);

        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction("UNDO", v -> {
            mList.add(itemAdapterPosition, del);
            adapter.restoreItem();
            if (isCurrentQueue)
                tm.restoreItem();
        });
        sb.show();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mList, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemMoved(int fromPos, int toPos) {
        if (isCurrentQueue)
            tm.updateActiveQueue(fromPos, toPos);
        isPlaylistDataModified = true;
    }

    @Override
    public void onClearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackground(recyclerView.getResources().getDrawable(android.R.color.transparent));
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<MusicModel> tracksToAdd = TrackPickerHelper.getInstance().giveTracks();
        if (null != tracksToAdd && tracksToAdd.size() > 0) {
            mList.addAll(tracksToAdd);
            isPlaylistDataModified = true;
            if (isCurrentQueue) tm.buildDataList(mList, tm.getActiveIndex());
        }
        if (mList.size() > 0 && null != adapter) adapter.addItems(mList);
    }

    @Override
    protected void onStop() {
        /*if (isPlaylistDataModified && isFavPlaylist) {
            PlaylistStorageManager.saveFavorite(this, mList);
            isPlaylistDataModified = false;
        }
        if (isPlaylistDataModified && !isCurrentQueue) {
            PlaylistStorageManager.updatePlaylistTracks(this, mList, mPlaylistCardIndex - 2);
            isPlaylistDataModified = false;
        }*/
        if (isPlaylistDataModified && isFavPlaylist) {
            DataManager.addFavoritesList(this, mList);
            isPlaylistDataModified = false;
        } else if (isPlaylistDataModified && !isCurrentQueue) {
            DataManager.updatePlaylistTrackAt(this, mList, mPlaylistCardIndex - 2);
            isPlaylistDataModified = false;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromMediaSession();
    }
}
