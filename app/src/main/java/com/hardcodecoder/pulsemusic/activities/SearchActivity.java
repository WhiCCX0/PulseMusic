package com.hardcodecoder.pulsemusic.activities;

import android.graphics.Color;
import android.media.session.MediaController;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.adapters.SearchAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.loaders.SearchQueryLoader;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends MediaSessionActivity implements ItemClickListener.Simple {

    private List<String> pendingUpdates = new ArrayList<>();
    private List<MusicModel> mSearchResult;
    private TextView tv;
    private SearchAdapter adapter;
    private TrackManager tm;
    private String mQuery = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.search_activity_close_btn).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        });

        tv = findViewById(R.id.result_empty);
        setUpSearchView();
        setRecyclerView();
        tm = TrackManager.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    private void setUpSearchView() {
        SearchView sv = findViewById(R.id.search_view);
        View v = sv.findViewById(R.id.search_plate);
        v.setBackgroundColor(Color.parseColor("#00000000"));
        sv.setIconified(false);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals(mQuery))
                    searchSuggestions(query);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSuggestions(newText);
                return true;
            }
        });
    }

    private void searchSuggestions(String query) {
        mQuery = query;
        pendingUpdates.add(mQuery);
        if (pendingUpdates.size() == 1)
            TaskRunner.executeAsync(new SearchQueryLoader(query), result -> {
                pendingUpdates.remove(0);
                this.mSearchResult = result;

                if (mSearchResult.size() <= 0) tv.setVisibility(View.VISIBLE);
                else tv.setVisibility(View.GONE);

                adapter.updateItems(result);

                if (pendingUpdates.size() > 0) {
                    searchSuggestions(pendingUpdates.get(pendingUpdates.size() - 1));
                    pendingUpdates.clear();
                }
            });
    }

    private void setRecyclerView() {
        RecyclerView rv = findViewById(R.id.search_rv);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new SearchAdapter(getLayoutInflater(), this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int pos) {
        tm.buildDataList(mSearchResult, pos);
        playMedia();
    }

    @Override
    public void onOptionsClick(View v, int position) {
        PopupMenu pm = new PopupMenu(this, v);
        pm.getMenuInflater().inflate(R.menu.menu_item_options, pm.getMenu());
        pm.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.id_play_next:
                    tm.playNext(mSearchResult.get(position));
                    break;
                case R.id.id_add_queue:
                    tm.addToActiveQueue(mSearchResult.get(position));
                    break;
                case R.id.id_add_playlist:
                    break;
                case R.id.info:
                    createDialog(position);
                    break;
                default:
            }
            return true;
        });
        pm.show();
    }

    private void createDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        MusicModel md = mSearchResult.get(pos);
        builder.setTitle(md.getTrackName());
        String s = getString(R.string.album_head) + " " + md.getAlbum() + "\n" + getString(R.string.artist_head) + " " + md.getArtist();
        builder.setMessage(s);
        builder.setPositiveButton(getString(R.string.done), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pendingUpdates.clear();
    }
}

