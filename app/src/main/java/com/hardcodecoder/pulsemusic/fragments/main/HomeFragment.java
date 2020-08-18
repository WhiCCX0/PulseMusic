package com.hardcodecoder.pulsemusic.fragments.main;

import android.app.Activity;
import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.FavoritesActivity;
import com.hardcodecoder.pulsemusic.activities.RecentActivity;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapter;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapter.LayoutStyle;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapterAlbum;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapterArtist;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final long BASE_DELAY_MILLS = 275;
    private static final int PICK_MUSIC = 1600;
    private MediaController.TransportControls mTransportControl;
    private TrackManager tm;

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tm = TrackManager.getInstance();

        if (LoaderCache.getAllTracksList().size() > 0 && null != getContext()) {
            LoaderHelper.loadTopAlbums(result -> loadTopAlbums(view, result));
            if (null == LoaderCache.getSuggestions())
                LoaderHelper.loadSuggestionsList(getContext().getContentResolver(), result -> loadSuggestions(view, result));
            else loadSuggestions(view, LoaderCache.getSuggestions());
            if (null == LoaderCache.getLatestTracks())
                LoaderHelper.loadLatestTracks(getContext().getContentResolver(), result -> loadLatestTracks(view, result));
            else loadLatestTracks(view, LoaderCache.getLatestTracks());
            LoaderHelper.loadTopArtist(result -> loadTopArtists(view, result));
        } else {
            MaterialTextView noTracksText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
            noTracksText.setText(getString(R.string.tracks_not_found));
        }

        view.findViewById(R.id.ic_recent).setOnClickListener(v -> startActivity(new Intent(getContext(), RecentActivity.class)));
        view.findViewById(R.id.ic_folder).setOnClickListener(v -> pickMedia());
        view.findViewById(R.id.ic_favorite).setOnClickListener(v -> startActivity(new Intent(getContext(), FavoritesActivity.class)));
    }

    private void loadTopAlbums(View view, List<TopAlbumModel> list) {
        if (null != list && list.size() > 0) {
            view.postDelayed(() -> {
                MaterialTextView topAlbumsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_top_albums_title)).inflate();
                topAlbumsTitle.setText(getString(R.string.top_albums));
                RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_top_albums_list)).inflate();
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
                rv.setHasFixedSize(true);
                HomeAdapterAlbum adapter = new HomeAdapterAlbum(list, getLayoutInflater(), (sharedView, position) -> {
                    if (null != getActivity()) {
                        TopAlbumModel albumModel = list.get(position);
                        NavigationUtil.goToAlbum(getActivity(), sharedView, albumModel.getAlbumName(), albumModel.getAlbumId(), albumModel.getAlbumArt());
                    }
                });
                SnapHelper helper = new PagerSnapHelper();
                helper.attachToRecyclerView(rv);
                rv.setAdapter(adapter);
            }, 0);
        }
    }

    private void loadSuggestions(View view, List<MusicModel> list) {
        if (null != list && list.size() > 0) {
            view.postDelayed(() -> {
                MaterialTextView suggestionsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_suggestions_title)).inflate();
                suggestionsTitle.setText(getString(R.string.random));
                RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_suggested_list)).inflate();
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
                rv.setHasFixedSize(true);
                HomeAdapter adapter = new HomeAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tm.buildDataList(list, position);
                        play();
                    }

                    @Override
                    public void onOptionsClick(int position) {
                        if (null != getActivity())
                            UIHelper.buildAndShowOptionsMenu(getActivity(), getActivity().getSupportFragmentManager(), list.get(position));
                    }
                }, LayoutStyle.ROUNDED_RECTANGLE);
                rv.setAdapter(adapter);
            }, BASE_DELAY_MILLS);
        }
    }

    private void loadLatestTracks(View view, List<MusicModel> list) {
        if (null != list && list.size() > 0) {
            view.postDelayed(() -> {
                MaterialTextView newInStoreTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_new_in_store_title)).inflate();
                newInStoreTitle.setText(getString(R.string.new_in_library));
                RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_new_in_store_list)).inflate();
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
                rv.setHasFixedSize(true);
                HomeAdapter adapter = new HomeAdapter(getLayoutInflater(), list, new SimpleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tm.buildDataList(list, position);
                        play();
                    }

                    @Override
                    public void onOptionsClick(int position) {
                        if (null != getActivity())
                            UIHelper.buildAndShowOptionsMenu(getActivity(), getActivity().getSupportFragmentManager(), list.get(position));
                    }
                }, LayoutStyle.ROUNDED_RECTANGLE);
                rv.setAdapter(adapter);
            }, BASE_DELAY_MILLS * 2);
        }
    }

    private void loadTopArtists(View view, List<TopArtistModel> list) {
        if (null != list && list.size() > 0) {
            view.postDelayed(() -> {
                MaterialTextView topAlbumsTitle = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_top_artists_title)).inflate();
                topAlbumsTitle.setText(getString(R.string.top_artist));
                RecyclerView rv = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_top_artists_list)).inflate();
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.HORIZONTAL, false));
                HomeAdapterArtist adapter = new HomeAdapterArtist(list, getLayoutInflater(), (sharedView, position) -> {
                    if (null != getActivity())
                        NavigationUtil.goToArtist(getActivity(), sharedView, list.get(position).getArtistName());
                });
                rv.setAdapter(adapter);
            }, BASE_DELAY_MILLS * 3);
        }
    }

    private void pickMedia() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, PICK_MUSIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_MUSIC) {
            MusicModel md = DataModelHelper.buildMusicModelFrom(getContext(), data);
            if (md != null) {
                List<MusicModel> singlePickedItemList = new ArrayList<>();
                singlePickedItemList.add(md);
                tm.buildDataList(singlePickedItemList, 0);
                play();
            }
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