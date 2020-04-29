package com.hardcodecoder.pulsemusic.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.DetailsActivity;
import com.hardcodecoder.pulsemusic.activities.FavoritesActivity;
import com.hardcodecoder.pulsemusic.activities.MainActivity;
import com.hardcodecoder.pulsemusic.activities.RecentActivity;
import com.hardcodecoder.pulsemusic.activities.SearchActivity;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapter;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapter.LayoutStyle;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapterAlbum;
import com.hardcodecoder.pulsemusic.adapters.HomeAdapterArtist;
import com.hardcodecoder.pulsemusic.helper.DataModelHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.AlbumsLoader;
import com.hardcodecoder.pulsemusic.loaders.ArtistsLoader;
import com.hardcodecoder.pulsemusic.loaders.LibraryLoader;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.viewmodel.HomeContentVM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int PICK_MUSIC = 1600;
    private MediaController.TransportControls mTransportControl;
    private List<MusicModel> mYouMayLikeList;
    private List<MusicModel> mNewInLibraryList;
    private List<AlbumModel> mAlbumList;
    private List<ArtistModel> mArtistList;
    private TrackManager tm;
    private HomeContentVM mModel;


    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        tm = TrackManager.getInstance();
        setHasOptionsMenu(true);
        mModel = HomeContentVM.getInstance();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        if (null != getActivity())
            ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            //openDrawer()
            HomeBottomSheetFragment homeBottomSheetFragment = HomeBottomSheetFragment.newInstance();
            homeBottomSheetFragment.show(getActivity().getSupportFragmentManager(), HomeBottomSheetFragment.TAG);
        });

        startPostponedEnterTransition();
        new Handler().postDelayed(() -> updateUi(view), 400); //wait for animation to complete before loading all list

        view.findViewById(R.id.ic_recent).setOnClickListener(v -> startActivity(new Intent(getContext(), RecentActivity.class)));
        view.findViewById(R.id.ic_folder).setOnClickListener(v -> pickMedia());
        view.findViewById(R.id.ic_favorite).setOnClickListener(v -> startActivity(new Intent(getContext(), FavoritesActivity.class)));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search)
            startActivity(new Intent(getContext(), SearchActivity.class));
        return true;
    }

    private void updateUi(View view) {
        mAlbumList = mModel.getAlbumsList();
        mYouMayLikeList = mModel.getYourMayLikeList();
        mNewInLibraryList = mModel.getNewInLibrary();
        mArtistList = mModel.getArtistList();

        if (null != getContext()) {
            ContentResolver contentResolver = getContext().getContentResolver();

            if (null == mAlbumList) {
                TaskRunner.executeAsync(new AlbumsLoader(contentResolver, SortOrder.ALBUMS.TITLE_ASC), (data) -> {
                    mAlbumList = data.subList(0, ((int) (data.size() * 0.2))); // sublist top 20%
                    Collections.shuffle(mAlbumList);
                    mModel.setAlbumsList(mAlbumList);
                    loadAlbumCard(view);
                });
            } else loadAlbumCard(view);


            if (null == mYouMayLikeList) {
                TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.TITLE_ASC), (data) -> {
                    mYouMayLikeList = data.subList(0, (int) (data.size() * 0.2));  //show only top 20%
                    Collections.shuffle(mYouMayLikeList);
                    mModel.setYourMayLikeList(mYouMayLikeList);
                    loadRecycleView(view, R.id.home_suggested_rv, mYouMayLikeList, LayoutStyle.CIRCLE);
                });
            } else
                loadRecycleView(view, R.id.home_suggested_rv, mYouMayLikeList, LayoutStyle.CIRCLE);


            if (null == mNewInLibraryList) {
                TaskRunner.executeAsync(new LibraryLoader(contentResolver, SortOrder.DATE_MODIFIED_DESC), (data) -> {
                    mNewInLibraryList = data.subList(0, (int) (data.size() * 0.2)); //show only top 20%
                    mModel.setNewInLibrary(mNewInLibraryList);
                    loadRecycleView(view, R.id.new_in_library_rv, mNewInLibraryList, LayoutStyle.ROUNDED_RECTANGLE);
                });
            } else
                loadRecycleView(view, R.id.new_in_library_rv, mNewInLibraryList, LayoutStyle.ROUNDED_RECTANGLE);

            if (null == mArtistList) {
                TaskRunner.executeAsync(new ArtistsLoader(contentResolver, SortOrder.ARTIST.NUM_OF_TRACKS_DESC), (data) -> {
                    mArtistList = data.subList(0, ((int) (data.size() * 0.2))); // sublist top 20%
                    Collections.shuffle(mArtistList);
                    mModel.setArtistList(mArtistList);
                    loadArtistRv(view);
                });
            } else loadArtistRv(view);
        } else Log.e("HomeFragment", "Context is null unable to fetch data");
    }

    private void loadRecycleView(View view, @IdRes int id, List<MusicModel> dataList, LayoutStyle style) {
        RecyclerView rv = view.findViewById(id);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        HomeAdapter adapter = new HomeAdapter(getLayoutInflater(), dataList, new ItemClickListener.Simple() {
            @Override
            public void onItemClick(int pos) {
                tm.buildDataList(dataList, pos);
                play();
            }

            @Override
            public void onOptionsClick(View view, int pos) {
                openMenu(dataList.get(pos), view);
            }
        }, style);
        rv.setAdapter(adapter);
    }

    private void loadArtistRv(View v) {
        RecyclerView rv = v.findViewById(R.id.home_recent_artist_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.HORIZONTAL, false));
        HomeAdapterArtist adapter = new HomeAdapterArtist(getLayoutInflater(), mArtistList, new ItemClickListener.Simple() {
            @Override
            public void onItemClick(int pos) {
            }

            @Override
            public void onOptionsClick(View view, int pos) {
                Intent i = new Intent(getContext(), DetailsActivity.class);
                i.putExtra(DetailsActivity.ALBUM_ID, 0);//No album id for artists
                i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ARTIST);
                i.putExtra(DetailsActivity.KEY_TITLE, mArtistList.get(pos).getArtistName());
                i.putExtra(DetailsActivity.KEY_ART_URL, "");
                Bundle b = null;
                if (null != getActivity())
                    b = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.home_iv_st)).toBundle();
                startActivity(i, b);
            }
        });
        rv.setAdapter(adapter);
    }

    private void loadAlbumCard(View v) {
        RecyclerView rv = v.findViewById(R.id.home_albums_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        HomeAdapterAlbum adapter = new HomeAdapterAlbum(getLayoutInflater(), mAlbumList, new ItemClickListener.Simple() {
            @Override
            public void onItemClick(int pos) {
            }

            @Override
            public void onOptionsClick(View view, int pos) {
                Intent i = new Intent(getContext(), DetailsActivity.class);
                i.putExtra(DetailsActivity.ALBUM_ID, mAlbumList.get(pos).getAlbumId());
                i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ALBUM);
                i.putExtra(DetailsActivity.KEY_TITLE, mAlbumList.get(pos).getAlbumName());
                i.putExtra(DetailsActivity.KEY_ART_URL, mAlbumList.get(pos).getAlbumArt());
                Bundle b = null;
                if (null != getActivity())
                    b = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.home_iv_st)).toBundle();
                startActivity(i, b);
            }
        });
        rv.setAdapter(adapter);
    }

    private void openMenu(MusicModel md, View v) {
        v.setBackground(v.getContext().getDrawable(R.drawable.active_item_background));
        PopupMenu pm = new PopupMenu(v.getContext(), v);
        pm.getMenuInflater().inflate(R.menu.item_overflow__menu, pm.getMenu());
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