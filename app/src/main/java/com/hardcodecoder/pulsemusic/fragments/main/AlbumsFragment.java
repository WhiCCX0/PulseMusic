package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.AlbumsAdapter;
import com.hardcodecoder.pulsemusic.fragments.main.base.CardGridFragment;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ALBUMS;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;

import java.util.List;

public class AlbumsFragment extends CardGridFragment {

    private GridLayoutManager mLayoutManager;
    private AlbumsAdapter mAdapter;
    private int mFirstVisibleItemPosition;
    //private boolean mAddOverlay = false;

    public static AlbumsFragment getInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int sortOrder = getCurrentSortOrder();
        ALBUMS albumsSortOrder = sortOrder == Preferences.SORT_ORDER_ASC ?
                ALBUMS.TITLE_ASC : ALBUMS.TITLE_DESC;
        LoaderHelper.loadAlbumsList(view.getContext().getContentResolver(),
                albumsSortOrder,
                result -> loadAlbumsList(view, result));
    }

    private void loadAlbumsList(View view, List<AlbumModel> list) {
        if (null != list && list.size() > 0) {
            /*if (null != getContext())
                mAddOverlay = AppSettings.isAlbumCardOverlayEnabled(getContext());*/
            RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_grid_rv)).inflate();
            mLayoutManager = new GridLayoutManager(rv.getContext(), getCurrentSpanCount());
            rv.setLayoutManager(mLayoutManager);
            rv.setHasFixedSize(true);
            mAdapter = new AlbumsAdapter(list,
                    getLayoutInflater(),
                    (sharedView, position) -> {
                        if (null != getActivity()) {
                            AlbumModel albumModel = list.get(position);
                            NavigationUtil.goToAlbum(getActivity(), sharedView, albumModel.getAlbumName(), albumModel.getAlbumId(), albumModel.getAlbumArt());
                        }
                    },
                    () -> mLayoutManager.scrollToPosition(mFirstVisibleItemPosition)/*, mAddOverlay*/);
            rv.setAdapter(mAdapter);
        } else {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        }
    }

    private ALBUMS resolveSortOrder(int sortOrder) {
        if (sortOrder == Preferences.SORT_ORDER_ASC)
            return ALBUMS.TITLE_ASC;
        return ALBUMS.TITLE_DESC;
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return super.getSortOrder();
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_ALBUMS_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSortOrder(resolveSortOrder(newSortOrder));
        if (null != getContext())
            AppSettings.saveSortOrder(getContext(), Preferences.SORT_ORDER_ALBUMS_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return super.getPortraitModeSpanCount();
        return AppSettings.getPortraitModeGridSpanCount(
                getContext(),
                Preferences.ALBUMS_SPAN_COUNT_PORTRAIT_KEY,
                Preferences.SPAN_COUNT_PORTRAIT_DEF_VALUE);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return super.getLandscapeModeSpanCount();
        return AppSettings.getLandscapeModeGridSpanCount(
                getContext(),
                Preferences.ALBUMS_SPAN_COUNT_LANDSCAPE_KEY,
                Preferences.SPAN_COUNT_LANDSCAPE_DEF_VALUE);
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
        if (null != getContext()) {
            if (configId == Configuration.ORIENTATION_PORTRAIT)
                AppSettings.savePortraitModeGridSpanCount(getContext(), Preferences.ALBUMS_SPAN_COUNT_PORTRAIT_KEY, spanCount);
            else if (configId == Configuration.ORIENTATION_LANDSCAPE)
                AppSettings.saveLandscapeModeGridSpanCount(getContext(), Preferences.ALBUMS_SPAN_COUNT_LANDSCAPE_KEY, spanCount);
        }
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        mLayoutManager.setSpanCount(spanCount);
    }
}
