package com.hardcodecoder.pulsemusic.adapters;

import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
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
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.List;

public class TrackPickerAdapter extends RecyclerView.Adapter<TrackPickerAdapter.TrackPickerSVH> {

    private List<MusicModel> mList;
    private LayoutInflater mInflater;
    private ItemClickListener.Selector mListener;
    private SparseBooleanArray booleanArray = new SparseBooleanArray();
    @DrawableRes
    private int drawable;
    private int lastPosition = -1;

    public TrackPickerAdapter(List<MusicModel> mList, LayoutInflater mInflater, ItemClickListener.Selector listener) {
        this.mList = mList;
        this.mInflater = mInflater;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public TrackPickerSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_track_picker_item, parent, false);
        TrackPickerSVH holder = new TrackPickerSVH(view);
        view.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            MusicModel md = mList.get(pos);

            if (!booleanArray.get(pos)) {
                mListener.onSelected(md);
                booleanArray.append(pos, true);
                drawable = R.drawable.selected_item_background;
            } else {
                mListener.onUnselected(md);
                booleanArray.append(pos, false);
                drawable = android.R.color.transparent;
            }
            //Use parent activity's context so that attr attributes are initialised
            //If parents activity's context is not used drawable attributes using "?attr/"
            // will be invalid as those are initialised under activity's theme
            v.setBackground(v.getContext().getDrawable(drawable));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackPickerSVH holder, int position) {
        holder.updateViewData(mList.get(position), booleanArray.get(holder.getAdapterPosition(), false));
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
        lastPosition = position;
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

    static class TrackPickerSVH extends RecyclerView.ViewHolder {

        private ImageView art;
        private TextView title, artist;

        TrackPickerSVH(@NonNull View itemView) {
            super(itemView);
            art = itemView.findViewById(R.id.album_art);
            title = itemView.findViewById(R.id.track_title);
            artist = itemView.findViewById(R.id.track_artist);
        }

        void updateViewData(MusicModel md, boolean selected) {
            GlideApp.with(itemView)
                    .load(md.getAlbumArtUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            art.setImageDrawable(MediaArtHelper.getMediaArtDrawable(itemView.getContext(), md.getAlbumId(), MediaArtHelper.RoundingRadius.RADIUS_8dp));
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(GlideConstantArtifacts.getRoundingRadiusSmall())
                    .into(art);
            title.setText(md.getSongName());
            artist.setText(md.getArtist());
            itemView.setBackground(itemView.getContext().getDrawable(selected ?
                    R.drawable.selected_item_background : android.R.color.transparent));
        }
    }

}
