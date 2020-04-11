package com.hardcodecoder.pulsemusic.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.GlideConstantArtifacts;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.model.AlbumModel;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsSVH> {

    private List<AlbumModel> data;
    private ItemClickListener.SingleEvent mListener;
    private LayoutInflater mInflater;

    public AlbumsAdapter(List<AlbumModel> list, LayoutInflater mInflater, ItemClickListener.SingleEvent mListener) {
        this.mInflater = mInflater;
        this.mListener = mListener;
        this.data = list;
    }

    @NonNull
    @Override
    public AlbumsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumsSVH(mInflater.inflate(R.layout.rv_grid_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsSVH holder, int position) {
        holder.setData(data.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != data)
            return data.size();
        return 0;
    }

    static class AlbumsSVH extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView art;

        AlbumsSVH(@NonNull View itemView, ItemClickListener.SingleEvent mListener) {
            super(itemView);
            art = itemView.findViewById(R.id.grid_item_iv);
            title = itemView.findViewById(R.id.grid_item_tv);
            itemView.setOnClickListener(v -> mListener.onClickItem(getAdapterPosition()));
        }

        void setData(AlbumModel am) {
            GlideApp
                    .with(itemView)
                    .load(am.getAlbumArt())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            MediaArtHelper.getMediaArtDrawableAsync(itemView.getContext(), am.getAlbumId(),
                                    MediaArtHelper.RoundingRadius.RADIUS_16dp,
                                    drawable -> art.setImageDrawable(drawable));
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(GlideConstantArtifacts.getRadius16dp())
                    .into(art);
            title.setText(am.getAlbumName());
        }
    }
}
