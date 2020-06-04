package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.RoundedBottomSheetDialog;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.model.TrackFileModel;
import com.hardcodecoder.pulsemusic.utils.DataUtils;

public class UIHelper {

    public static void buildSongInfoDialog(Context context, final MusicModel musicModel) {
        BottomSheetDialog bottomSheetDialog = new RoundedBottomSheetDialog(context);
        View view = View.inflate(context, R.layout.bottom_sheet_track_info, null);
        bottomSheetDialog.setContentView(view);
        view.findViewById(R.id.dialog_ok).setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing())
                bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();

        TrackFileModel infoModel = DataModelHelper.getTrackInfo(view.getContext(), musicModel);
        if (null != infoModel) {
            final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            MaterialTextView displayTextView = view.findViewById(R.id.dialog_display_name);
            displayTextView.setText(infoModel.getDisplayName());

            MaterialTextView trackTitle = view.findViewById(R.id.dialog_track_title);
            setInfo(trackTitle, context.getString(R.string.head_title), musicModel.getTrackName(), styleSpan);

            MaterialTextView trackAlbum = view.findViewById(R.id.dialog_track_album);
            setInfo(trackAlbum, context.getString(R.string.head_album), musicModel.getAlbum(), styleSpan);

            MaterialTextView trackArtist = view.findViewById(R.id.dialog_track_artist);
            setInfo(trackArtist, context.getString(R.string.head_artist), musicModel.getArtist(), styleSpan);

            MaterialTextView trackFileSize = view.findViewById(R.id.dialog_file_size);
            setInfo(trackFileSize, context.getString(R.string.head_file_size), DataUtils.getFormattedFileSize(infoModel.getFileSize()), styleSpan);

            MaterialTextView trackFileType = view.findViewById(R.id.dialog_file_type);
            setInfo(trackFileType, context.getString(R.string.head_file_type), infoModel.getFileType(), styleSpan);

            MaterialTextView trackBitRate = view.findViewById(R.id.dialog_bitrate);
            setInfo(trackBitRate, context.getString(R.string.head_bitrate), DataUtils.getFormattedBitRate(infoModel.getBitRate()), styleSpan);

            MaterialTextView trackSampleRate = view.findViewById(R.id.dialog_sample_rate);
            setInfo(trackSampleRate, context.getString(R.string.head_sample_rate), DataUtils.getFormattedSampleRate(infoModel.getSampleRate()), styleSpan);

            MaterialTextView trackChannelCount = view.findViewById(R.id.dialog_channel_count);
            setInfo(trackChannelCount, context.getString(R.string.head_channel_count), String.valueOf(infoModel.getChannelCount()), styleSpan);
        }
    }

    private static void setInfo(@NonNull MaterialTextView textView, @NonNull String head, @NonNull String info, @NonNull StyleSpan styleSpan) {
        String text = String.format("%s: %s", head, info);
        SpannableString artist = new SpannableString(text);
        artist.setSpan(styleSpan, 0, head.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(artist);
    }
}
