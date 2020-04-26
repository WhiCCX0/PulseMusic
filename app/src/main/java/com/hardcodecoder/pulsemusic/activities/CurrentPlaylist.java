package com.hardcodecoder.pulsemusic.activities;

import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentPlaylist extends MediaSessionActivity implements ClickDragRvListener, RecyclerViewGestures.GestureCallback {

    private TrackManager tm;
    private List<MusicModel> mCurrentList = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;
    private PlaylistDataAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_data);

        tm = TrackManager.getInstance();
        mCurrentList.addAll(tm.getActiveQueue());

        Toolbar t = findViewById(R.id.toolbar);
        t.setTitle(R.string.playlist_current_queue);
        t.setNavigationOnClickListener(v -> finish());

        if (mCurrentList.size() > 0) {
            RecyclerView recyclerView = findViewById(R.id.playlist_data_rv);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mAdapter = new PlaylistDataAdapter(mCurrentList, getLayoutInflater(), this);
            recyclerView.setAdapter(mAdapter);

            ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(this);
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
    public void initiateDrag(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
        holder.itemView.setBackground(getDrawable(R.drawable.active_item_background));
    }

    @Override
    public void onItemSwiped(int itemAdapterPosition) {
        if (itemAdapterPosition == tm.getActiveIndex()) {
            Toast.makeText(this, "Cannot remove active item", Toast.LENGTH_SHORT).show();
            mAdapter.notifyItemChanged(itemAdapterPosition);
        } else if (tm.canRemoveItem(itemAdapterPosition)) {

            final MusicModel deletedItem = mCurrentList.remove(itemAdapterPosition);
            final int deletedIndex = itemAdapterPosition;
            mAdapter.removeItem(deletedIndex);
            tm.removeItemFromActiveQueue(deletedIndex);

            Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
            sb.setAction("UNDO", v -> {
                mAdapter.restoreItem(deletedItem, deletedIndex);
                mCurrentList.add(deletedIndex, deletedItem);
                tm.restoreItem(deletedIndex, deletedItem);
            });
            sb.show();
        } else Toast.makeText(this, "Cannot remove active item", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mCurrentList, fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemMoved(int fromPos, int toPos) {
        tm.updateActiveQueue(fromPos, toPos);
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
                        mCurrentList.addAll(modelList);
                        mAdapter.addItems(modelList);
                        tm.buildDataList(mCurrentList, tm.getActiveIndex());
                    }
                });
            }
        }
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
