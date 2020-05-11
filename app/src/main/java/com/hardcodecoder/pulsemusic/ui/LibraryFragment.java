package com.hardcodecoder.pulsemusic.ui;

import android.media.session.MediaController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.dialog.RoundedBottomSheetDialog;
import com.hardcodecoder.pulsemusic.interfaces.LibraryItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryFragment extends Fragment implements LibraryItemClickListener {

    public static final int LIBRARY_SORT_ORDER_TITLE_ASC = 5000;
    private static final int LIBRARY_SORT_ORDER_TITLE_DESC = 5001;
    private MediaController.TransportControls mTransportControl;
    private List<MusicModel> mList;
    private LibraryAdapter mAdapter;
    private TrackManager tm;
    private int mCurrentSortOrder;

    public LibraryFragment() {
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
        mList = new ArrayList<>(tm.getMainList());
        mCurrentSortOrder = AppSettings.getLibraryFragmentSortOrder(view.getContext());
        if (mCurrentSortOrder == LIBRARY_SORT_ORDER_TITLE_DESC)
            Collections.reverse(mList);
        if (mList.size() > 0) {
            RecyclerView recyclerView = view.findViewById(R.id.rv_library_fragment);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            mAdapter = new LibraryAdapter(mList, getLayoutInflater(), this);
            recyclerView.setAdapter(mAdapter);
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
                changeSortOrder(LIBRARY_SORT_ORDER_TITLE_ASC);
                break;
            case R.id.menu_action_sort_desc:
                changeSortOrder(LIBRARY_SORT_ORDER_TITLE_DESC);
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
                AppSettings.saveLibraryFragmentSortOrder(getContext(), newSortOrder);
        }
    }

    @Override
    public void onItemClick(int pos) {
        tm.buildDataList(mList, pos);
        play();
    }

    @Override
    public void onOptionsClick(int pos) {
        showMenuItems(mList.get(pos));
    }

    private void play() {
        if (null == mTransportControl && null != getActivity()) {
            mTransportControl = getActivity().getMediaController().getTransportControls();
            mTransportControl.play();
        } else
            mTransportControl.play();
    }

    private void showMenuItems(MusicModel md) {
        View view = View.inflate(getContext(), R.layout.library_item_menu, null);
        BottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(view.getContext());

        view.findViewById(R.id.track_play_next)
                .setOnClickListener(v -> {
                    tm.playNext(md);
                    Toast.makeText(v.getContext(), getString(R.string.play_next_toast), Toast.LENGTH_SHORT).show();
                    if (bottomSheetDialog.isShowing())
                        bottomSheetDialog.dismiss();
                });

        view.findViewById(R.id.add_to_queue)
                .setOnClickListener(v -> {
                    tm.addToActiveQueue(md);
                    Toast.makeText(v.getContext(), getString(R.string.add_to_queue_toast), Toast.LENGTH_SHORT).show();
                    if (bottomSheetDialog.isShowing())
                        bottomSheetDialog.dismiss();
                });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}
