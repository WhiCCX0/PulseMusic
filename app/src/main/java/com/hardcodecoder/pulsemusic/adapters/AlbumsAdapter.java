package com.hardcodecoder.pulsemusic.adapters;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.GlideConstantArtifacts;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.AlbumModel;
import com.hardcodecoder.pulsemusic.utils.DimensionsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsSVH> {

    private final Handler mHandler = new Handler();
    private List<AlbumModel> mList;
    private SimpleTransitionClickListener mListener;
    private LayoutInflater mInflater;
    private boolean mAddOverlay;

    public AlbumsAdapter(List<AlbumModel> list, LayoutInflater mInflater, boolean addOverlay, SimpleTransitionClickListener mListener) {
        this.mList = list;
        this.mInflater = mInflater;
        this.mListener = mListener;
        this.mAddOverlay = addOverlay;
    }

    public void changeOverlayOption(boolean changed) {
        if (mAddOverlay != changed) {
            mAddOverlay = changed;
            notifyItemRangeChanged(0, mList.size());
        }
    }

    public void updateSortOrder() {
        TaskRunner.executeAsync(() -> {
            List<AlbumModel> oldSortedTracks = new ArrayList<>(this.mList);
            Collections.reverse(mList);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldSortedTracks.size();
                }

                @Override
                public int getNewListSize() {
                    return mList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).equals(mList.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).getAlbumName().equals(mList.get(newItemPosition).getAlbumName());
                }
            });
            mHandler.post(() -> diffResult.dispatchUpdatesTo(this));
        });
    }

    @NonNull
    @Override
    public AlbumsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumsSVH(mInflater.inflate(R.layout.rv_grid_item_album, parent, false), mAddOverlay, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsSVH holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class AlbumsSVH extends RecyclerView.ViewHolder {

        private ImageView albumArt;
        private TextView title;

        AlbumsSVH(@NonNull View itemView, boolean addOverlay, SimpleTransitionClickListener mListener) {
            super(itemView);
            if (addOverlay)
                ((ViewStub) itemView.findViewById(R.id.stub_album_art_overlay)).inflate();
            albumArt = itemView.findViewById(R.id.grid_item_iv);
            title = itemView.findViewById(R.id.grid_item_tv);
            itemView.setOnClickListener(v -> mListener.onItemClick(albumArt, getAdapterPosition()));
        }

        void setData(AlbumModel am) {
            albumArt.setTransitionName("shared_transition_album_iv_" + getAdapterPosition());
            GlideApp.with(itemView)
                    .load(am.getAlbumArt())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            MediaArtHelper.setDynamicAlbumArtOnLoadFailed(albumArt, am.getAlbumId(), DimensionsUtil.RoundingRadius.RADIUS_8dp);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(GlideConstantArtifacts.getRadius8dp())
                    .transition(GenericTransitionOptions.with(R.anim.fade_in_image))
                    .into(albumArt);
            title.setText(am.getAlbumName());
        }
    }
}
