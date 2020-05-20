package com.hardcodecoder.pulsemusic.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.ArtistModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistSVH> {

    private final Handler mHandler = new Handler();
    private List<ArtistModel> mList;
    private LayoutInflater mInflater;
    private SimpleTransitionClickListener mListener;

    public ArtistAdapter(List<ArtistModel> list, LayoutInflater mInflater, SimpleTransitionClickListener mListener) {
        this.mList = list;
        this.mInflater = mInflater;
        this.mListener = mListener;
    }

    public void updateSortOrder() {
        TaskRunner.executeAsync(() -> {
            List<ArtistModel> oldSortedTracks = new ArrayList<>(this.mList);
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
                    return oldSortedTracks.get(oldItemPosition).getArtistName().equals(mList.get(newItemPosition).getArtistName());
                }
            });
            mHandler.post(() -> diffResult.dispatchUpdatesTo(this));
        });
    }

    @NonNull
    @Override
    public ArtistAdapter.ArtistSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistSVH(mInflater.inflate(R.layout.rv_grid_item_artist, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistSVH holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class ArtistSVH extends RecyclerView.ViewHolder {

        private ImageView artistArt;
        private TextView title;

        ArtistSVH(@NonNull View itemView, SimpleTransitionClickListener mListener) {
            super(itemView);
            artistArt = itemView.findViewById(R.id.grid_item_artist_iv);
            title = itemView.findViewById(R.id.grid_item_artist_tv);
            itemView.setOnClickListener(v -> mListener.onItemClick(artistArt, getAdapterPosition()));
        }

        void setData(ArtistModel am) {
            artistArt.setTransitionName("shared_transition_artist_iv_" + getAdapterPosition());
            title.setText(am.getArtistName());
        }
    }
}
