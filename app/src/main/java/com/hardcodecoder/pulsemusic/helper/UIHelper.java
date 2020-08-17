package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.AddToPlaylistDialog;
import com.hardcodecoder.pulsemusic.dialog.RoundedBottomSheetDialog;
import com.hardcodecoder.pulsemusic.interfaces.CreatePlaylist;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.singleton.TrackManager;
import com.hardcodecoder.pulsemusic.storage.AppFileManager;
import com.hardcodecoder.pulsemusic.utils.DataUtils;

public class UIHelper {

    public static void buildAndShowOptionsMenu(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull final MusicModel md) {
        final TrackManager tm = TrackManager.getInstance();
        View view = View.inflate(context, R.layout.library_item_menu, null);
        BottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(view.getContext());

        view.findViewById(R.id.track_play_next).setOnClickListener(v -> {
            tm.playNext(md);
            Toast.makeText(v.getContext(), context.getString(R.string.play_next_toast), Toast.LENGTH_SHORT).show();
            dismiss(bottomSheetDialog);
        });

        view.findViewById(R.id.add_to_queue).setOnClickListener(v -> {
            tm.addToActiveQueue(md);
            Toast.makeText(v.getContext(), context.getString(R.string.add_to_queue_toast), Toast.LENGTH_SHORT).show();
            dismiss(bottomSheetDialog);
        });

        view.findViewById(R.id.song_info).setOnClickListener(v -> {
            buildSongInfoDialog(context, md);
            dismiss(bottomSheetDialog);
        });

        view.findViewById(R.id.add_to_playlist).setOnClickListener(v -> {
            openAddToPlaylistDialog(fragmentManager, md);
            dismiss(bottomSheetDialog);
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public static void buildCreatePlaylistDialog(@NonNull Context context, @NonNull CreatePlaylist callback) {
        BottomSheetDialog sheetDialog = new RoundedBottomSheetDialog(context);
        View layout = View.inflate(context, R.layout.bottom_dialog_edit_text, null);
        sheetDialog.setContentView(layout);

        TextView header = layout.findViewById(R.id.header);
        header.setText(context.getString(R.string.create_playlist));

        TextInputLayout til = layout.findViewById(R.id.edit_text_container);
        til.setHint(context.getString(R.string.create_playlist_hint));

        TextInputEditText et = layout.findViewById(R.id.text_input_field);

        layout.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (et.getText() != null && et.getText().toString().length() > 0) {
                String playlistName = et.getText().toString();
                AppFileManager.savePlaylist(playlistName);
                callback.onPlaylistCreated(playlistName);
            } else {
                Toast.makeText(context, context.getString(R.string.create_playlist_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            if (sheetDialog.isShowing())
                sheetDialog.dismiss();
        });

        layout.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            if (sheetDialog.isShowing())
                sheetDialog.dismiss();
        });
        sheetDialog.show();
    }

    private static void buildSongInfoDialog(Context context, final MusicModel musicModel) {
        BottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(context);
        View view = View.inflate(context, R.layout.bottom_sheet_track_info, null);
        bottomSheetDialog.setContentView(view);
        view.findViewById(R.id.dialog_ok).setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing())
                bottomSheetDialog.dismiss();
        });
        // Reference view fields which needs to be filled with data
        MaterialTextView displayTextView = view.findViewById(R.id.dialog_display_name);
        MaterialTextView trackTitle = view.findViewById(R.id.dialog_track_title);
        MaterialTextView trackAlbum = view.findViewById(R.id.dialog_track_album);
        MaterialTextView trackArtist = view.findViewById(R.id.dialog_track_artist);
        MaterialTextView trackFileSize = view.findViewById(R.id.dialog_file_size);
        MaterialTextView trackFileType = view.findViewById(R.id.dialog_file_type);
        MaterialTextView trackBitRate = view.findViewById(R.id.dialog_bitrate);
        MaterialTextView trackSampleRate = view.findViewById(R.id.dialog_sample_rate);
        MaterialTextView trackChannelCount = view.findViewById(R.id.dialog_channel_count);
        final Handler handler = new Handler();
        DataModelHelper.getTrackInfo(view.getContext(), musicModel, infoModel -> {
            if (null != infoModel) {
                handler.post(() -> {
                    final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                    displayTextView.setText(infoModel.getDisplayName());
                    setInfo(trackTitle, context.getString(R.string.head_title), musicModel.getTrackName(), styleSpan);
                    setInfo(trackAlbum, context.getString(R.string.head_album), musicModel.getAlbum(), styleSpan);
                    setInfo(trackArtist, context.getString(R.string.head_artist), musicModel.getArtist(), styleSpan);
                    setInfo(trackFileSize, context.getString(R.string.head_file_size), DataUtils.getFormattedFileSize(infoModel.getFileSize()), styleSpan);
                    setInfo(trackFileType, context.getString(R.string.head_file_type), infoModel.getFileType(), styleSpan);
                    setInfo(trackBitRate, context.getString(R.string.head_bitrate), DataUtils.getFormattedBitRate(infoModel.getBitRate()), styleSpan);
                    setInfo(trackSampleRate, context.getString(R.string.head_sample_rate), DataUtils.getFormattedSampleRate(infoModel.getSampleRate()), styleSpan);
                    setInfo(trackChannelCount, context.getString(R.string.head_channel_count), String.valueOf(infoModel.getChannelCount()), styleSpan);
                    bottomSheetDialog.show();
                });
            }
        });
    }

    private static void setInfo(@NonNull MaterialTextView textView, @NonNull String head, @NonNull String info, @NonNull StyleSpan styleSpan) {
        String text = String.format("%s: %s", head, info);
        SpannableString artist = new SpannableString(text);
        artist.setSpan(styleSpan, 0, head.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(artist);
    }

    private static void openAddToPlaylistDialog(FragmentManager fragmentManager, final MusicModel itemToAdd) {
        AddToPlaylistDialog dialog = AddToPlaylistDialog.getInstance();
        Bundle b = new Bundle();
        b.putSerializable(AddToPlaylistDialog.MUSIC_MODEL_KEY, itemToAdd);
        dialog.setArguments(b);
        dialog.show(fragmentManager, AddToPlaylistDialog.TAG);
    }

    private static void dismiss(BottomSheetDialog dialog) {
        if (dialog.isShowing())
            dialog.dismiss();
    }
}
