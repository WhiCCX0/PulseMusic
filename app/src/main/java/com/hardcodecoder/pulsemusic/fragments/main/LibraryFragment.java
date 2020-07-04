package com.hardcodecoder.pulsemusic.fragments.main;

import android.media.session.MediaController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryFragment extends Fragment implements SimpleItemClickListener {

    private MediaController.TransportControls mTransportControl;
    private List<MusicModel> mList;
    private LibraryAdapter mAdapter;
    private TrackManager tm;
    private int mCurrentSortOrder;

    public static LibraryFragment getInstance() {
        return new LibraryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        tm = TrackManager.getInstance();
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mList = new ArrayList<>(LoaderCache.getAllTracksList());
        mCurrentSortOrder = AppSettings.getSortOrder(view.getContext(), Preferences.SORT_ORDER_LIBRARY_KEY);
        if (mCurrentSortOrder == Preferences.SORT_ORDER_DESC)
            Collections.reverse(mList);
        if (mList.size() > 0) {
            view.post(() -> {
                RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_library_fragment_rv)).inflate();
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                mAdapter = new LibraryAdapter(mList, getLayoutInflater(), this);
                recyclerView.setAdapter(mAdapter);
            });
        } else {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library, menu);
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
            case R.id.menu_action_grid_size:
                break;
        }
        return true;
    }

    private void changeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            mAdapter.updateSortOrder();
            mCurrentSortOrder = newSortOrder;
            if (null != getContext())
                AppSettings.saveSortOrder(getContext(), Preferences.SORT_ORDER_LIBRARY_KEY, newSortOrder);
        }
    }

    @Override
    public void onItemClick(int pos) {
        tm.buildDataList(mList, pos);
        play();
    }

    @Override
    public void onOptionsClick(int pos) {
        if (null != getActivity()) {
            UIHelper.buildAndShowOptionsMenu(getActivity(), getActivity().getSupportFragmentManager(), mList.get(pos));
        }
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
