package com.hardcodecoder.pulsemusic.fragments.main;

import android.content.res.Configuration;
import android.media.session.MediaController;
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
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.fragments.main.base.ListGridFragment;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryFragment extends ListGridFragment {

    private MediaController.TransportControls mTransportControl;
    private List<MusicModel> mList;
    private GridLayoutManager mLayoutManager;
    private LibraryAdapter mAdapter;
    private TrackManager tm;
    private int mFirstVisibleItemPosition;

    public static LibraryFragment getInstance() {
        return new LibraryFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tm = TrackManager.getInstance();
        mList = new ArrayList<>(LoaderCache.getAllTracksList());
        final int sortOrder = getCurrentSortOrder();

        if (sortOrder == Preferences.SORT_ORDER_DESC)
            Collections.reverse(mList);

        if (mList.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_library_fragment_rv)).inflate();
            mLayoutManager = new GridLayoutManager(recyclerView.getContext(), getCurrentSpanCount());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            mAdapter = new LibraryAdapter(
                    mList,
                    getLayoutInflater(),
                    new SimpleItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            tm.buildDataList(mList, position);
                            play();
                        }

                        @Override
                        public void onOptionsClick(int position) {
                            if (null != getActivity()) {
                                UIHelper.buildAndShowOptionsMenu(getActivity(), getActivity().getSupportFragmentManager(), mList.get(position));
                            }
                        }
                    },
                    () -> mLayoutManager.scrollToPosition(mFirstVisibleItemPosition));
            recyclerView.setAdapter(mAdapter);
        } else {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        }
    }

    private SortOrder resolveSortOrder(int sortOrder) {
        if (sortOrder == Preferences.SORT_ORDER_ASC)
            return SortOrder.TITLE_ASC;
        return SortOrder.TITLE_DESC;
    }

    @Override
    public int getSortOrder() {
        if (null == getContext())
            return Preferences.SORT_ORDER_ASC;
        return AppSettings.getSortOrder(getContext(), Preferences.SORT_ORDER_LIBRARY_KEY);
    }

    @Override
    public void onSortOrderChanged(int newSortOrder) {
        mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        mAdapter.updateSortOrder(resolveSortOrder(newSortOrder));
        if (null != getContext())
            AppSettings.saveSortOrder(getContext(), Preferences.SORT_ORDER_LIBRARY_KEY, newSortOrder);
    }

    @Override
    public int getPortraitModeSpanCount() {
        if (null == getContext())
            return Preferences.SPAN_COUNT_LIBRARY_PORTRAIT_DEF_VALUE;
        return AppSettings.getPortraitModeGridSpanCount(
                getContext(),
                Preferences.LIBRARY_SPAN_COUNT_PORTRAIT_KEY,
                Preferences.SPAN_COUNT_LIBRARY_PORTRAIT_DEF_VALUE);
    }

    @Override
    public int getLandscapeModeSpanCount() {
        if (null == getContext())
            return Preferences.SPAN_COUNT_LIBRARY_LANDSCAPE_DEF_VALUE;
        return AppSettings.getLandscapeModeGridSpanCount(
                getContext(),
                Preferences.LIBRARY_SPAN_COUNT_LANDSCAPE_KEY,
                Preferences.SPAN_COUNT_LIBRARY_LANDSCAPE_DEF_VALUE);
    }

    @Override
    public void saveNewSpanCount(int configId, int spanCount) {
        if (null != getContext()) {
            if (configId == Configuration.ORIENTATION_PORTRAIT)
                AppSettings.savePortraitModeGridSpanCount(getContext(), Preferences.LIBRARY_SPAN_COUNT_PORTRAIT_KEY, spanCount);
            else if (configId == Configuration.ORIENTATION_LANDSCAPE)
                AppSettings.saveLandscapeModeGridSpanCount(getContext(), Preferences.LIBRARY_SPAN_COUNT_LANDSCAPE_KEY, spanCount);
        }
    }

    @Override
    public void onLayoutSpanCountChanged(int currentOrientation, int spanCount) {
        mLayoutManager.setSpanCount(spanCount);
    }

    private void play() {
        if (mTransportControl != null) {
            mTransportControl.play();
        } else if (getActivity() != null) {
            mTransportControl = getActivity().getMediaController().getTransportControls();
            mTransportControl.play();
        }
    }
}
