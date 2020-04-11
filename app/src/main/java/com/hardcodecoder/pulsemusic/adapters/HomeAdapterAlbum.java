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

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.GlideConstantArtifacts;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.helper.MediaArtHelper;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.model.AlbumModel;

import java.util.List;

public class HomeAdapterAlbum extends RecyclerView.Adapter<HomeAdapterAlbum.AdapterSVH> {

    private LayoutInflater mInflater;
    private List<AlbumModel> mList;
    private ItemClickListener.Simple mListener;

    public HomeAdapterAlbum(LayoutInflater inflater, List<AlbumModel> list, ItemClickListener.Simple listener) {
        this.mInflater = inflater;
        this.mList = list;
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

        private ImageView iv;
        private TextView tv;

        AdapterSVH(@NonNull View itemView, ItemClickListener.Simple listener) {
            super(itemView);
            tv = itemView.findViewById(R.id.rv_item_title);
            iv = itemView.findViewById(R.id.iv_album_card);
            itemView.setOnClickListener(v -> listener.onOptionsClick(iv, getAdapterPosition()));
        }

        void updateData(AlbumModel am) {
            GlideApp.with(iv)
                    .load(am.getAlbumArt())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            MediaArtHelper.getMediaArtDrawableAsync(itemView.getContext(), am.getAlbumId(),
                                    MediaArtHelper.RoundingRadius.RADIUS_4dp,
                                    drawable -> iv.setImageDrawable(drawable));
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(new CenterCrop(), GlideConstantArtifacts.getRadius16dp())
                    .transition(GenericTransitionOptions.with(R.anim.fade_in_image))
                    .into(iv);
            tv.setText(am.getAlbumName());
        }
    }
}
