package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SingleClickListener;

import java.util.List;

public class ATPAdapter extends RecyclerView.Adapter<ATPAdapter.ATPViewHolder> {

    private LayoutInflater mInflater;
    private List<String> mDataList;
    private SingleClickListener mListener;

    public ATPAdapter(LayoutInflater inflater, List<String> dataList, SingleClickListener listener) {
        mInflater = inflater;
        mDataList = dataList;
        mListener = listener;
    }

    public void addItem(String name) {
        mDataList.add(0, name);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ATPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ATPViewHolder(mInflater.inflate(R.layout.rv_item_playlist_names, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ATPViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null == mDataList)
            return 0;
        return mDataList.size();
    }

    static class ATPViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView mPlaylistName;

        ATPViewHolder(@NonNull View itemView, SingleClickListener listener) {
            super(itemView);
            mPlaylistName = itemView.findViewById(R.id.rv_item_playlist_name);
            itemView.setOnClickListener(v -> listener.onItemCLick(getAdapterPosition()));
        }

        void setData(String name) {
            mPlaylistName.setText(name);
        }
    }
}
