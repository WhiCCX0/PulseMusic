package com.hardcodecoder.pulsemusic.adapters;

import android.content.res.Configuration;
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
import com.hardcodecoder.pulsemusic.helper.PMBGridAdapterDiffCallback;
import com.hardcodecoder.pulsemusic.interfaces.GridAdapterCallback;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.loaders.SortOrder;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistSVH> {

    private List<ArtistModel> mList;
    private LayoutInflater mInflater;
    private SimpleTransitionClickListener mListener;
    private GridAdapterCallback mCallback;
    private int mOrientation;
    private int mCurrentSpanCount;
    private int mLayoutId;

    public ArtistAdapter(List<ArtistModel> list, LayoutInflater inflater,
                         SimpleTransitionClickListener listener,
                         GridAdapterCallback callback,
                         int orientation,
                         int spanCount) {
        mList = list;
        mInflater = inflater;
        mListener = listener;
        mCallback = callback;
        mOrientation = orientation;
        mCurrentSpanCount = spanCount;
        mLayoutId = getLayoutId();
    }

    public void updateSortOrder(SortOrder.ARTIST sortOrder) {
        final Handler handler = new Handler();
        TaskRunner.executeAsync(() -> {
            List<ArtistModel> oldSortedTracks = new ArrayList<>(mList);
            List<ArtistModel> updatedTracks = SortUtil.sortArtistList(mList, sortOrder);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PMBGridAdapterDiffCallback(oldSortedTracks, updatedTracks) {
                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldSortedTracks.get(oldItemPosition).getArtistName().equals(updatedTracks.get(newItemPosition).getArtistName());
                }
            });
            handler.post(() -> {
                diffResult.dispatchUpdatesTo(ArtistAdapter.this);
                mCallback.onSortUpdateComplete();
            });
        });
    }

    public void updateSpanCount(int orientation, int newSpanCount) {
        mOrientation = orientation;
        mCurrentSpanCount = newSpanCount;
        mLayoutId = getLayoutId();
    }

    private int getLayoutId() {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT && mCurrentSpanCount >= 3 ||
                mOrientation == Configuration.ORIENTATION_LANDSCAPE && mCurrentSpanCount >= 5) {
            return R.layout.rv_grid_item_artist_small;
        }
        return R.layout.rv_grid_item_artist;
    }

    @NonNull
    @Override
    public ArtistAdapter.ArtistSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistSVH(mInflater.inflate(mLayoutId, parent, false), mListener);
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
