package com.hardcodecoder.pulsemusic.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface TrackPickerListener {
    void onItemClick(RecyclerView.ViewHolder viewHolder, int position, boolean isSelected);
}
