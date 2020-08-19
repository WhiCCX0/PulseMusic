package com.hardcodecoder.pulsemusic.activities;

import android.graphics.Color;
import android.media.session.MediaController;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.LibraryAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends MediaSessionActivity implements SimpleItemClickListener {

    private TrackManager tm;
    private List<MusicModel> favoritesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_tracks);

        tm = TrackManager.getInstance();

        Handler handler = new Handler();
        AppFileManager.getFavorites(result -> handler.post(() -> {
            if (null != result && result.size() > 0) {
                findViewById(R.id.no_tracks_found).setVisibility(View.GONE);
                favoritesList = new ArrayList<>(result);
                RecyclerView rv = (RecyclerView) ((ViewStub) findViewById(R.id.stub_playlist_tracks_rv)).inflate();
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL, false));
                LibraryAdapter adapter = new LibraryAdapter(favoritesList, getLayoutInflater(), this, null);
                rv.setAdapter(adapter);
            } else {
                MaterialTextView tv = findViewById(R.id.no_tracks_found);
                String text = getString(R.string.no_favorites_tracks);
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                stringBuilder.setSpan(new ForegroundColorSpan(Color.RED), text.length() - 1, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                tv.setText(stringBuilder);
            }
        }));

        MaterialToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle(getString(R.string.favorites));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.open_track_picker_btn).setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int pos) {
        tm.buildDataList(favoritesList, pos);
        playMedia();
    }

    @Override
    public void onOptionsClick(int pos) {
        UIHelper.buildAndShowOptionsMenu(this, getSupportFragmentManager(), favoritesList.get(pos));
    }

    @Override
    public void onMediaServiceConnected(MediaController controller) {
    }
}
