package com.hardcodecoder.pulsemusic.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.DetailsActivity;
import com.hardcodecoder.pulsemusic.activities.FavoritesActivity;
import com.hardcodecoder.pulsemusic.activities.RecentActivity;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapter;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapter.LayoutStyle;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapterAlbum;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapterArtist;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.loaders.LoaderHelper;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int PICK_MUSIC = 1600;
    private MediaController.TransportControls mTransportControl;
    private TrackManager tm;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tm = TrackManager.getInstance();

        new Handler().postDelayed(() -> updateUi(view), 400); //wait for animation to complete before loading all list

        view.findViewById(R.id.ic_recent).setOnClickListener(v -> startActivity(new Intent(getContext(), RecentActivity.class)));
        view.findViewById(R.id.ic_folder).setOnClickListener(v -> pickMedia());
        view.findViewById(R.id.ic_favorite).setOnClickListener(v -> startActivity(new Intent(getContext(), FavoritesActivity.class)));
    }

    private void updateUi(View view) {
        if (null != getContext()) {
            ContentResolver contentResolver = getContext().getContentResolver();

            if (null == LoaderCache.getTopAlbums())
                LoaderHelper.loadTopAlbums(contentResolver, result -> loadTopAlbums(view, result));
            else loadTopAlbums(view, LoaderCache.getTopAlbums());

            if (null == LoaderCache.getSuggestions())
                LoaderHelper.loadSuggestionsList(result -> loadSuggestions(view, result));
            else loadSuggestions(view, LoaderCache.getSuggestions());

            if (null == LoaderCache.getLatestTracks())
                LoaderHelper.loadLatestTracks(contentResolver, result -> loadLatestTracks(view, result));
            else loadLatestTracks(view, LoaderCache.getLatestTracks());

            if (null == LoaderCache.getTopArtists())
                LoaderHelper.loadTopArtist(contentResolver, result -> loadArtistRv(view, result));
            else loadArtistRv(view, LoaderCache.getTopArtists());

        } else Log.e("HomeFragment", "Context is null unable to fetch data");
    }

    private void loadTopAlbums(View v, List<AlbumModel> list) {
        RecyclerView rv = v.findViewById(R.id.home_albums_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        HomeAdapterAlbum adapter = new HomeAdapterAlbum(list, getLayoutInflater(), (imageView, position) -> {
            Intent i = new Intent(getContext(), DetailsActivity.class);
            i.putExtra(DetailsActivity.ALBUM_ID, list.get(position).getAlbumId());
            i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ALBUM);
            i.putExtra(DetailsActivity.KEY_TITLE, list.get(position).getAlbumName());
            i.putExtra(DetailsActivity.KEY_ART_URL, list.get(position).getAlbumArt());
            if (null != getActivity()) {
                String transitionName = imageView.getTransitionName();
                i.putExtra(DetailsActivity.KEY_TRANSITION_NAME, transitionName);
                Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageView, transitionName).toBundle();
                startActivity(i, b);
            }
        });
        rv.setAdapter(adapter);
    }

    private void loadSuggestions(View view, List<MusicModel> list) {
        RecyclerView rv = view.findViewById(R.id.home_suggested_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        HomeAdapter adapter = new HomeAdapter(getLayoutInflater(), list, new ItemClickListener.Simple() {
            @Override
            public void onItemClick(int pos) {
                tm.buildDataList(list, pos);
                play();
            }

            @Override
            public void onOptionsClick(View view, int pos) {
                openMenu(list.get(pos), view);
            }
        }, LayoutStyle.CIRCLE);
        rv.setAdapter(adapter);
    }

    private void loadLatestTracks(View view, List<MusicModel> list) {
        RecyclerView rv = view.findViewById(R.id.new_in_library_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        HomeAdapter adapter = new HomeAdapter(getLayoutInflater(), list, new ItemClickListener.Simple() {
            @Override
            public void onItemClick(int pos) {
                tm.buildDataList(list, pos);
                play();
            }

            @Override
            public void onOptionsClick(View view, int pos) {
                openMenu(list.get(pos), view);
            }
        }, LayoutStyle.ROUNDED_RECTANGLE);
        rv.setAdapter(adapter);
    }

    private void loadArtistRv(View v, List<ArtistModel> list) {
        RecyclerView rv = v.findViewById(R.id.home_recent_artist_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.HORIZONTAL, false));
        HomeAdapterArtist adapter = new HomeAdapterArtist(list, getLayoutInflater(), (imageView, position) -> {
            Intent i = new Intent(getContext(), DetailsActivity.class);
            i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ARTIST);
            i.putExtra(DetailsActivity.KEY_TITLE, list.get(position).getArtistName());
            if (null != getActivity()) {
                String transitionName = imageView.getTransitionName();
                i.putExtra(DetailsActivity.KEY_TRANSITION_NAME, transitionName);
                Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageView, transitionName).toBundle();
                startActivity(i, b);
            }
        });
        rv.setAdapter(adapter);
    }

    private void openMenu(MusicModel md, View v) {
        v.setBackground(v.getContext().getDrawable(R.drawable.active_item_background));
        PopupMenu pm = new PopupMenu(v.getContext(), v);
        pm.getMenuInflater().inflate(R.menu.menu_item_options, pm.getMenu());
        pm.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.id_play_next:
                    tm.playNext(md);
                    Toast.makeText(v.getContext(), getString(R.string.play_next_toast), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.id_add_queue:
                    tm.addToActiveQueue(md);
                    Toast.makeText(v.getContext(), getString(R.string.add_to_queue_toast), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.id_add_playlist:
                    break;
                default:
            }
            return true;
        });
        pm.setOnDismissListener(menu -> v.setBackground(v.getResources().getDrawable(android.R.color.transparent)));
        pm.show();
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
            play();
        }
    }
}