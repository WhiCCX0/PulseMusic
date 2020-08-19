package com.hardcodecoder.pulsemusic.fragments.main.base;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class PMBGridFragment extends Fragment {

    private int mCurrentOrientation;
    private int mSpanCount;
    private int mCurrentSortOrder;

    void initialize() {
        mCurrentOrientation = getResources().getConfiguration().orientation;
        mSpanCount = mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ?
                getPortraitModeSpanCount() : getLandscapeModeSpanCount();
        mCurrentSortOrder = getSortOrder();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final int newOrientation = newConfig.orientation;
        if (newOrientation != mCurrentOrientation) {
            mCurrentOrientation = newOrientation;
            if (newOrientation == Configuration.ORIENTATION_LANDSCAPE)
                mSpanCount = getLandscapeModeSpanCount();
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                mSpanCount = getPortraitModeSpanCount();

            onLayoutSpanCountChanged(mCurrentOrientation, mSpanCount);
        }
        if (null != getActivity())
            getActivity().invalidateOptionsMenu();
    }

    protected int getCurrentOrientation() {
        return mCurrentOrientation;
    }

    protected int getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    protected int getCurrentSpanCount() {
        return mSpanCount;
    }

    void changeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mCurrentSortOrder = newSortOrder;
            onSortOrderChanged(newSortOrder);
        }
    }

    void updateGridSpanCount(int id, int spanCount) {
        saveNewSpanCount(id, spanCount);
        onLayoutSpanCountChanged(id, spanCount);
    }

    public abstract int getSortOrder();

    public abstract int getPortraitModeSpanCount();

    public abstract int getLandscapeModeSpanCount();

    public abstract void saveNewSpanCount(int configId, int spanCount);

    public abstract void onSortOrderChanged(int newSortOrder);

    public abstract void onLayoutSpanCountChanged(int currentOrientation, int spanCount);
}
