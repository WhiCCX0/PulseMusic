package com.hardcodecoder.pulsemusic.helper;

import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerCallbackAdapter;

public class RecyclerViewSelectorHelper {

    private TrackPickerCallbackAdapter mAdapter;

    public RecyclerViewSelectorHelper(TrackPickerCallbackAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void onItemSelected(RecyclerView.ViewHolder viewHolder, int position) {
        mAdapter.onItemSelected(position);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemTouchHelperViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemTouchHelperViewHolder.onItemSelected();
        }
    }

    public void onItemUnSelected(RecyclerView.ViewHolder viewHolder, int position) {
        mAdapter.onItemUnselected(position);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ItemTouchHelperViewHolder itemTouchHelperViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemTouchHelperViewHolder.onItemClear();
        }
    }
}
