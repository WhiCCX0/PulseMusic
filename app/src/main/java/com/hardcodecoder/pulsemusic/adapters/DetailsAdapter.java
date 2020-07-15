package com.hardcodecoder.pulsemusic.adapters;

import android.graphics.drawable.Drawable;
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
import com.hardcodecoder.pulsemusic.interfaces.SimpleItemClickListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsSVH> {

    private List<MusicModel> mList;
    private SimpleItemClickListener listener;
    private LayoutInflater mInflater;

    public DetailsAdapter(List<MusicModel> mList, SimpleItemClickListener listener, LayoutInflater mInflater) {
        this.mList = mList;
        this.listener = listener;
        this.mInflater = mInflater;
    }

    @NonNull
    @Override
    public DetailsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailsSVH(mInflater.inflate(R.layout.list_item_with_options, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsSVH holder, int position) {
        holder.updateData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class DetailsSVH extends RecyclerView.ViewHolder {

        private ImageView albumArt;
        private MaterialTextView title, subTitle;

        DetailsSVH(@NonNull View itemView, SimpleItemClickListener mListener) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.list_item_option_album_art);
            title = itemView.findViewById(R.id.list_item_title);
            subTitle = itemView.findViewById(R.id.list_item_sub_title);
            itemView.findViewById(R.id.list_item_option_options_btn).setOnClickListener(v -> mListener.onOptionsClick(getAdapterPosition()));
            itemView.setOnClickListener(v -> mListener.onItemClick(getAdapterPosition()));
        }

        void updateData(MusicModel md) {
            title.setText(md.getTrackName());
            subTitle.setText(md.getArtist());
            GlideApp
                    .with(itemView.getContext())
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
    }
}
