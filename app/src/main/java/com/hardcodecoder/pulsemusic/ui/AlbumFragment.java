package com.hardcodecoder.pulsemusic.ui;

import android.content.Intent;
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
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.DetailsActivity;
import com.hardcodecoder.pulsemusic.adapters.AlbumsAdapter;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.loaders.SortOrder.ALBUMS;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

import java.util.List;

public class AlbumFragment extends Fragment {

    public static final int ALBUMS_SORT_ORDER_TITLE_ASC = 6000;
    private static final int ALBUMS_SORT_ORDER_TITLE_DESC = 6001;
    private AlbumsAdapter adapter;
    private int spanCount;
    private int currentConfig;
    private GridLayoutManager layoutManager;
    private int mCurrentSortOrder;

    public AlbumFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        currentConfig = getResources().getConfiguration().orientation;
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            spanCount = AppSettings.getLandscapeGridSpanCount(getContext());
        else
            spanCount = AppSettings.getPortraitGridSpanCount(getContext());

        if (null != getContext()) {
            mCurrentSortOrder = AppSettings.getAlbumsFragmentSortOrder(getContext());
            ALBUMS sortOrder = (mCurrentSortOrder == ALBUMS_SORT_ORDER_TITLE_DESC) ? ALBUMS.TITLE_DESC : ALBUMS.TITLE_ASC;

            LoaderHelper.loadAlbumsList(sortOrder, result -> loadAlbumsList(view, result));
        }
    }

    private void loadAlbumsList(View view, List<AlbumModel> list) {
        RecyclerView rv = view.findViewById(R.id.rv_album_fragment);
        rv.setVisibility(View.VISIBLE);
        layoutManager = new GridLayoutManager(rv.getContext(), spanCount);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        adapter = new AlbumsAdapter(list, getLayoutInflater(), (sharedView, position) -> {
            Intent i = new Intent(getContext(), DetailsActivity.class);
            i.putExtra(DetailsActivity.ALBUM_ID, list.get(position).getAlbumId());
            i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ALBUM);
            i.putExtra(DetailsActivity.KEY_TITLE, list.get(position).getAlbumName());
            i.putExtra(DetailsActivity.KEY_ART_URL, list.get(position).getAlbumArt());
            if (null != getActivity()) {
                String transitionName = sharedView.getTransitionName();
                i.putExtra(DetailsActivity.KEY_TRANSITION_NAME, transitionName);
                Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName).toBundle();
                startActivity(i, b);
            }
        });
        rv.setAdapter(adapter);
    }

    private void changeSortOrder(int newSortOrder) {
        if (mCurrentSortOrder != newSortOrder) {
            adapter.updateSortOrder();
            mCurrentSortOrder = newSortOrder;
            if (null != getContext())
                AppSettings.saveAlbumsFragmentSortOrder(getContext(), newSortOrder);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_album_artist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_sort_asc:
                changeSortOrder(ALBUMS_SORT_ORDER_TITLE_ASC);
                break;
            case R.id.menu_action_sort_desc:
                changeSortOrder(ALBUMS_SORT_ORDER_TITLE_DESC);
                break;
            case R.id.two:
                updateGridSize(ID.PORTRAIT, 2);
                break;
            case R.id.three:
                updateGridSize(ID.PORTRAIT, 3);
                break;
            case R.id.four:
                updateGridSize(ID.PORTRAIT, 4);
                break;
            case R.id.l_four:
                updateGridSize(ID.LANDSCAPE, 4);
                break;
            case R.id.l_five:
                updateGridSize(ID.LANDSCAPE, 5);
                break;
            case R.id.l_six:
                updateGridSize(ID.LANDSCAPE, 6);
                break;
        }
        return false;
    }

    private void updateGridSize(ID id, int spanCount) {
        if (id == ID.PORTRAIT) {
            AppSettings.savePortraitGridSpanCount(getContext(), spanCount);
            if (currentConfig == Configuration.ORIENTATION_PORTRAIT)
                layoutManager.setSpanCount(spanCount);
        } else {
            AppSettings.saveLandscapeGridSpanCount(getContext(), spanCount);
            if (currentConfig == Configuration.ORIENTATION_LANDSCAPE)
                layoutManager.setSpanCount(spanCount);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            currentConfig = Configuration.ORIENTATION_LANDSCAPE;
            spanCount = AppSettings.getLandscapeGridSpanCount(getContext());
            layoutManager.setSpanCount(spanCount);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            currentConfig = Configuration.ORIENTATION_PORTRAIT;
            spanCount = AppSettings.getPortraitGridSpanCount(getContext());
            layoutManager.setSpanCount(spanCount);
        }
    }

    private enum ID {
        PORTRAIT,
        LANDSCAPE
    }
}
