package com.hardcodecoder.pulsemusic.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
import com.hardcodecoder.pulsemusic.interfaces.ClickDragRvListener;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDataAdapter extends RecyclerView.Adapter<PlaylistDataAdapter.PlaylistDataSVH> {

    private LayoutInflater mInflater;
    private List<MusicModel> mPlaylistTracks = new ArrayList<>();
    private ClickDragRvListener mListener;
    private int lastPosition = -1;

    public PlaylistDataAdapter(List<MusicModel> playlistTracks, LayoutInflater inflater, ClickDragRvListener mListener) {
        this.mPlaylistTracks.addAll(playlistTracks);
        this.mInflater = inflater;
        this.mListener = mListener;
    }

    public void addItems(final List<MusicModel> list) {
        int startIndex = mPlaylistTracks.size();
        mPlaylistTracks.addAll(list);
        notifyItemRangeInserted(startIndex, list.size());
    }

    public void removeItem(int pos) {
        mPlaylistTracks.remove(pos);
        notifyItemRemoved(pos);
    }

    public void restoreItem(MusicModel musicModel, int deletedPosition) {
        mPlaylistTracks.add(deletedPosition, musicModel);
        notifyItemInserted(deletedPosition);
    }

    @NonNull
    @Override
    public PlaylistDataSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistDataSVH(mInflater.inflate(R.layout.rv_playlist_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDataSVH holder, int position) {
        holder.updateView(mPlaylistTracks.get(position));
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top));
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        return mPlaylistTracks.size();
    }

    static class PlaylistDataSVH extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView art;

        PlaylistDataSVH(@NonNull View itemView, ClickDragRvListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.library_item_tv1);
            art = itemView.findViewById(R.id.library_item_iv1);
            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
            //noinspection AndroidLintClickableViewAccessibility
            itemView.findViewById(R.id.btn_handle)
                    .setOnTouchListener((v, event) -> {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_UP)
                            listener.initiateDrag(this);
                        return true;
                    });
        }

        void updateView(MusicModel md) {
            title.setText(md.getSongName());
            GlideApp
                    .with(itemView.getContext())
                    .load(md.getAlbumArtUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            MediaArtHelper.getMediaArtDrawableAsync(itemView.getContext(), md.getAlbumId(),
                                    MediaArtHelper.RoundingRadius.RADIUS_4dp,
                                    drawable -> art.setImageDrawable(drawable));
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(GlideConstantArtifacts.getRadius8dp())
                    .into(art);
        }
    }
}
