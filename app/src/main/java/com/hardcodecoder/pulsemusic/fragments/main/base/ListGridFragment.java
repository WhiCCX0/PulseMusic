package com.hardcodecoder.pulsemusic.fragments.main.base;

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

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;

public class ListGridFragment extends PMBGridFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        initialize();
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getShowOptionsMenuItemSort()) {
            if (getCurrentOrientation() == Configuration.ORIENTATION_PORTRAIT)
                inflater.inflate(R.menu.menu_library, menu);
            else if (getCurrentOrientation() == Configuration.ORIENTATION_LANDSCAPE)
                inflater.inflate(R.menu.menu_library_land, menu);
        } else {
            if (getCurrentOrientation() == Configuration.ORIENTATION_PORTRAIT)
                inflater.inflate(R.menu.menu_library_no_sort, menu);
            else if (getCurrentOrientation() == Configuration.ORIENTATION_LANDSCAPE)
                inflater.inflate(R.menu.menu_library_no_sort_land, menu);
        }
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
            case R.id.one:
                updateGridSpanCount(Configuration.ORIENTATION_PORTRAIT, 1);
                break;
            case R.id.two:
                updateGridSpanCount(Configuration.ORIENTATION_PORTRAIT, 2);
                break;
            case R.id.l_two:
                updateGridSpanCount(Configuration.ORIENTATION_LANDSCAPE, 2);
                break;
            case R.id.l_three:
                updateGridSpanCount(Configuration.ORIENTATION_LANDSCAPE, 3);
                break;
            case R.id.l_four:
                updateGridSpanCount(Configuration.ORIENTATION_LANDSCAPE, 4);
                break;
        }
        return true;
    }

    protected boolean getShowOptionsMenuItemSort() {
        return true;
    }

    @Override
    public int getSortOrder() {
        return Preferences.SORT_ORDER_ASC;
    }

    @Override
    public int getPortraitModeSpanCount() {
        return Preferences.SPAN_COUNT_LIBRARY_PORTRAIT_DEF_VALUE;
    }

    @Override
    public int getLandscapeModeSpanCount() {
        return Preferences.SPAN_COUNT_LIBRARY_LANDSCAPE_DEF_VALUE;
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
    }
}
