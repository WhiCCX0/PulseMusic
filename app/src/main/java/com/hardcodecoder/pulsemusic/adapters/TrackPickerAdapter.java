package com.hardcodecoder.pulsemusic.adapters;

import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.GlideConstantArtifacts;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerCallbackAdapter;
import com.hardcodecoder.pulsemusic.interfaces.TrackPickerListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.util.List;

public class TrackPickerAdapter extends RecyclerView.Adapter<TrackPickerAdapter.TrackPickerSVH> implements TrackPickerCallbackAdapter {

    private final SparseBooleanArray mSelectedItemState = new SparseBooleanArray();
    private List<MusicModel> mList;
    private LayoutInflater mInflater;
    private TrackPickerListener mListener;

    public TrackPickerAdapter(List<MusicModel> mList, LayoutInflater mInflater, TrackPickerListener listener) {
        this.mList = mList;
        this.mInflater = mInflater;
        this.mListener = listener;
    }

    @Override
    public void onItemSelected(int position) {
        mSelectedItemState.put(position, true);
    }

    @Override
    public void onItemUnselected(int position) {
        mSelectedItemState.put(position, false);
    }

    @NonNull
    @Override
    public TrackPickerSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackPickerSVH(mInflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackPickerSVH holder, int position) {
        boolean isSelected = mSelectedItemState.get(position, false);
        holder.itemView.setOnClickListener(v ->
                mListener.onItemClick(holder, holder.getAdapterPosition(), mSelectedItemState.get(holder.getAdapterPosition(), false)));

        if (isSelected)
            holder.itemView.setBackground(ImageUtil.getTintedGradientOverlay(holder.itemView.getContext()));
        else
            holder.itemView.setBackground(holder.itemView.getContext().getDrawable(android.R.color.transparent));

        holder.updateViewData(mList.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TrackPickerSVH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        if (mList != null) return mList.size();
        else return 0;
    }

    static class TrackPickerSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private ImageView albumArt;
        private MaterialTextView title, artist;

        TrackPickerSVH(@NonNull View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.list_item_album_art);
            title = itemView.findViewById(R.id.list_item_title);
            artist = itemView.findViewById(R.id.list_item_sub_title);
        }

        void updateViewData(MusicModel md) {
            title.setText(md.getTrackName());
            artist.setText(md.getArtist());

            GlideApp.with(itemView)
                    .load(md.getAlbumArtUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            MediaArtHelper.setDynamicAlbumArtOnLoadFailed(albumArt, md.getAlbumId(), DimensionsUtil.RoundingRadius.RADIUS_8dp);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(new MultiTransformation<>(GlideConstantArtifacts.getCenterCrop(), GlideConstantArtifacts.getRadius8dp()))
                    .into(albumArt);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getTintedGradientOverlay(itemView.getContext()));
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(itemView.getContext().getDrawable(android.R.color.transparent));
        }
    }
}
