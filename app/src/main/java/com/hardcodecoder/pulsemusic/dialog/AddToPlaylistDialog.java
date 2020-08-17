package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.adapters.ATPAdapter;
import com.hardcodecoder.pulsemusic.helper.UIHelper;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;

import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialog extends RoundedBottomSheetDialogFragment {

    public static final String TAG = "AddToPlaylistDialog";
    public static final String MUSIC_MODEL_KEY = "data";
    private ATPAdapter mAdapter;
    private MaterialTextView mNoItemText;
    private List<String> mPlaylistNames;
    private MusicModel mItemToAdd = null;

    public static AddToPlaylistDialog getInstance() {
        return new AddToPlaylistDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null)
            mItemToAdd = (MusicModel) getArguments().getSerializable(MUSIC_MODEL_KEY);
        return inflater.inflate(R.layout.bottom_sheet_dialog_add_to_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpRecyclerView(view);
        view.findViewById(R.id.atp_create_playlist_btn).setOnClickListener(v -> {
            if (null != getActivity())
                UIHelper.buildCreatePlaylistDialog(getActivity(), playlistName -> {
                    if (null == mAdapter) setUpRecyclerView(view);
                    else view.post(() -> mAdapter.addItem(playlistName));
                });
        });
    }

    private void setUpRecyclerView(View view) {
        AppFileManager.getPlaylists(result -> {
            if (null == result || result.size() <= 0) {
                mNoItemText = (MaterialTextView) ((ViewStub) view.findViewById(R.id.stub_no_tracks_found)).inflate();
                mNoItemText.setText(getString(R.string.atp_no_user_playlist_found));
                return;
            }
            mPlaylistNames = new ArrayList<>(result);
            view.post(() -> {
                if (null != mNoItemText) mNoItemText.setVisibility(View.GONE);
                RecyclerView recyclerView = (RecyclerView) ((ViewStub) view.findViewById(R.id.stub_vertical_recycler_view)).inflate();
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false));
                mAdapter = new ATPAdapter(getLayoutInflater(), mPlaylistNames, position -> {
                    if (null == mItemToAdd) {
                        Toast.makeText(view.getContext(), getString(R.string.atp_track_add_error_toast), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AppFileManager.addItemToPlaylist(mPlaylistNames.get(position), mItemToAdd);
                    Toast.makeText(view.getContext(), getString(R.string.atp_track_added_toast), Toast.LENGTH_SHORT).show();
                    if (isVisible())
                        dismiss();
                });
                recyclerView.setAdapter(mAdapter);
            });
        });
    }
}
