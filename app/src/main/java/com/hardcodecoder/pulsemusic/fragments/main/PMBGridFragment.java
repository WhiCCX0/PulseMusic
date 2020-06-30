package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;

public abstract class PMBGridFragment extends Fragment {

    private int mCurrentOrientation;
    private int mSpanCount;
    private int mCurrentSortOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mCurrentOrientation = getResources().getConfiguration().orientation;
        mSpanCount = mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ?
                getPortraitModeSpanCount() : getLandscapeModeSpanCount();
        mCurrentSortOrder = getSortOrder();
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT)
            inflater.inflate(R.menu.menu_grid_fragment, menu);
        else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            inflater.inflate(R.menu.menu_grid_fragment_land, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_sort_asc:
                changeSortOrder(Preferences.SORT_ORDER_ASC);
                break;
            case R.id.menu_action_sort_desc:
                changeSortOrder(Preferences.SORT_ORDER_DESC);
                break;
            case R.id.two:
                updateGridSpanCount(Configuration.ORIENTATION_PORTRAIT, 2);
                break;
            case R.id.three:
                updateGridSpanCount(Configuration.ORIENTATION_PORTRAIT, 3);
                break;
            case R.id.four:
                updateGridSpanCount(Configuration.ORIENTATION_PORTRAIT, 4);
                break;
            case R.id.l_four:
                updateGridSpanCount(Configuration.ORIENTATION_LANDSCAPE, 4);
                break;
            case R.id.l_five:
                updateGridSpanCount(Configuration.ORIENTATION_LANDSCAPE, 5);
                break;
            case R.id.l_six:
                updateGridSpanCount(Configuration.ORIENTATION_LANDSCAPE, 6);
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCurrentOrientation = Configuration.ORIENTATION_LANDSCAPE;
            mSpanCount = getLandscapeModeSpanCount();
            onLayoutSpanCountChanged(mSpanCount);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
            mSpanCount = getPortraitModeSpanCount();
            onLayoutSpanCountChanged(mSpanCount);
        }
        if (null != getActivity())
            getActivity().invalidateOptionsMenu();
    }

    int getCurrentOrientation() {
        return mCurrentOrientation;
    }

    int getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    int getCurrentSpanCount() {
        return mSpanCount;
    }

    private void changeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mCurrentSortOrder = newSortOrder;
            onSortOrderChanged(newSortOrder);
        }
    }

    private void updateGridSpanCount(int id, int spanCount) {
        saveNewSpanCount(id, spanCount);
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT)
            onLayoutSpanCountChanged(spanCount);
        if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE)
            onLayoutSpanCountChanged(spanCount);
    }

    public abstract int getSortOrder();

    public abstract void onSortOrderChanged(int newSortOrder);

    public abstract int getPortraitModeSpanCount();

    public abstract int getLandscapeModeSpanCount();

    public abstract void saveNewSpanCount(int configId, int spanCount);

    public abstract void onLayoutSpanCountChanged(int spanCount);
}
