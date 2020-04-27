package com.hardcodecoder.pulsemusic.ui;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.CurrentPlaylistActivity;
import com.hardcodecoder.pulsemusic.activities.MainActivity;
import com.hardcodecoder.pulsemusic.activities.PlaylistDetailsActivity;
import com.hardcodecoder.pulsemusic.activities.SearchActivity;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.adapters.CardsAdapter;
import com.hardcodecoder.pulsemusic.helper.RecyclerViewGestureHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.interfaces.SimpleGestureCallback;
import com.hardcodecoder.pulsemusic.storage.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment implements ItemClickListener.Cards, SimpleGestureCallback {

    private CardsAdapter mAdapter;
    private Context mContext;
    private List<String> mPlaylistNames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (getContext() != null)
            mContext = getContext();
        return inflater.inflate(R.layout.fragment_playlist_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = view.findViewById(R.id.btn_add_playlist);
        fab.setOnClickListener(v -> createPlaylist(false, -1 /*Creating new item*/));

        mPlaylistNames = new ArrayList<>();
        mPlaylistNames.add(getString(R.string.playlist_current_queue));

        StorageHelper.getPlaylists(mContext, result -> {
            mPlaylistNames.addAll(result);
            loadPlaylistCards(view);
        });

        Toolbar t = view.findViewById(R.id.toolbar);
        t.setTitle(R.string.playlist_nav);
        if (null != getActivity())
            ((MainActivity) getActivity()).setSupportActionBar(t);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_playlist_card_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;

            case R.id.menu_action_equalizer:
                final Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                if (null != getContext()) {
                    if ((intent.resolveActivity(getContext().getPackageManager()) != null))
                        startActivityForResult(intent, 599);
                    else
                        Toast.makeText(getContext(), getString(R.string.equalizer_error), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menu_action_setting:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
        }
        return true;
    }

    private void loadPlaylistCards(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.playlist_cards_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.item_falls_down_animation);
        recyclerView.setLayoutAnimation(controller);
        mAdapter = new CardsAdapter(mPlaylistNames, getLayoutInflater(), this, this);
        recyclerView.setAdapter(mAdapter);

        /*
         * Adding swipe gesture to delete playlist card
         */
        ItemTouchHelper.Callback itemTouchHelperCallback = new RecyclerViewGestureHelper(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addNewPlaylist(String playlistName) {
        mPlaylistNames.add(playlistName);
        mAdapter.notifyItemInserted(mPlaylistNames.size() - 1);
        StorageHelper.savePlaylist(mContext, playlistName);
    }

    private void createPlaylist(boolean isEdit, int pos) {
        if (getContext() != null) {
            View layout = View.inflate(mContext, R.layout.dialog_create_playlist, null);

            BottomSheetDialog sheetDialog = new CustomBottomSheet(mContext);
            sheetDialog.setContentView(layout);
            sheetDialog.show();

            TextView header = layout.findViewById(R.id.header);
            header.setText(getResources().getString(isEdit ? R.string.edit_playlist : R.string.create_playlist));

            TextInputLayout til = layout.findViewById(R.id.edit_text_container);
            til.setHint(getResources().getString(R.string.create_playlist_hint));

            TextInputEditText et = layout.findViewById(R.id.text_input_field);

            Button create = layout.findViewById(R.id.confirm_btn);
            create.setOnClickListener(v -> {
                if (et.getText() != null && et.getText().toString().length() > 0) {
                    if (isEdit) {
                        String oldName = mPlaylistNames.remove(pos);
                        String newName = et.getText().toString();
                        mPlaylistNames.add(pos, newName);
                        mAdapter.notifyItemChanged(pos);
                        StorageHelper.renamePlaylist(mContext, oldName, newName);
                    } else addNewPlaylist(et.getText().toString());
                } else {
                    Toast.makeText(mContext, "Please enter playlist's name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sheetDialog.isShowing())
                    sheetDialog.dismiss();
            });

            Button cancel = layout.findViewById(R.id.cancel_btn);
            cancel.setOnClickListener(v -> {
                if (sheetDialog.isShowing())
                    sheetDialog.dismiss();
            });
        }
    }


    @Override
    public void onItemClick(int pos) {
        if (pos == 0)
            startActivity(new Intent(mContext, CurrentPlaylistActivity.class));
        else {
            Intent i = new Intent(mContext, PlaylistDetailsActivity.class);
            i.putExtra(PlaylistDetailsActivity.KEY_TITLE, mPlaylistNames.get(pos));
            startActivity(i);
        }
    }

    @Override
    public void onEdit(int pos) {
        createPlaylist(true, pos);
    }

    /*@Override
    public void onItemSwiped(int itemAdapterPosition) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("Delete Playlist: " + mPlaylistNames.get(itemAdapterPosition) + " ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    StorageHelper.deletePlaylist(mContext, mPlaylistNames.get(itemAdapterPosition));
                    Toast.makeText(getContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("NO", (dialog, which) -> mAdapter.notifyItemChanged(itemAdapterPosition));
        alertDialog.create().show();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onItemMoved(int fromPos, int toPos) {
    }

    @Override
    public void onClearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
    }*/

    @Override
    public void onItemDismissed(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("Delete Playlist: " + mPlaylistNames.get(position) + " ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    StorageHelper.deletePlaylist(mContext, mPlaylistNames.get(position));
                    Toast.makeText(getContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("NO", (dialog, which) -> mAdapter.notifyItemChanged(position));
        alertDialog.create().show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
    }
}
