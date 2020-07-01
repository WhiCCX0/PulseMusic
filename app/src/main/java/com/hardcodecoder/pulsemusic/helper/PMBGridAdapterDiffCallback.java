package com.hardcodecoder.pulsemusic.helper;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public abstract class PMBGridAdapterDiffCallback extends DiffUtil.Callback {

    private List<?> mOldList;
    private List<?> mUpdatedList;

    protected PMBGridAdapterDiffCallback(List<?> oldList, List<?> updatedList) {
        this.mOldList = oldList;
        this.mUpdatedList = updatedList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mUpdatedList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mUpdatedList.get(newItemPosition));
    }
}
