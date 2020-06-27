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
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.TopAlbumModel;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

import java.util.List;

public class HomeAdapterAlbum extends RecyclerView.Adapter<HomeAdapterAlbum.AdapterSVH> {

    private List<TopAlbumModel> mList;
    private LayoutInflater mInflater;
    private SimpleTransitionClickListener mListener;

    public HomeAdapterAlbum(List<TopAlbumModel> list, LayoutInflater inflater, SimpleTransitionClickListener listener) {
        this.mList = list;
        this.mInflater = inflater;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public HomeAdapterAlbum.AdapterSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeAdapterAlbum.AdapterSVH(mInflater.inflate(R.layout.rv_album_card_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapterAlbum.AdapterSVH holder, int position) {
        holder.updateData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class AdapterSVH extends RecyclerView.ViewHolder {

        private ImageView albumArt;
        private MaterialTextView title;

        AdapterSVH(@NonNull View itemView, SimpleTransitionClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.home_rv_album_album_name);
            albumArt = itemView.findViewById(R.id.home_rv_album_album_art);
            itemView.setOnClickListener(v -> listener.onItemClick(albumArt, getAdapterPosition()));
        }

        void updateData(TopAlbumModel albumModel) {
            albumArt.setTransitionName("shared_transition_album_iv_" + getAdapterPosition());
            GlideApp.with(albumArt)
                    .load(albumModel.getAlbumArt())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            MediaArtHelper.setDynamicAlbumArtOnLoadFailed(albumArt, albumModel.getAlbumId(), DimensionsUtil.RoundingRadius.RADIUS_8dp);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(new MultiTransformation<>(GlideConstantArtifacts.getCenterCrop(), GlideConstantArtifacts.getRadius8dp()))
                    .into(albumArt);
            title.setText(albumModel.getAlbumName());
        }
    }
}
