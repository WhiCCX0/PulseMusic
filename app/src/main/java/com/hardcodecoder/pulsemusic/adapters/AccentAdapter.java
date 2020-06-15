package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SingleClickListener;
import com.hardcodecoder.pulsemusic.model.AccentsModel;
import com.hardcodecoder.pulsemusic.views.ColorView;

import java.util.List;

public class AccentAdapter extends RecyclerView.Adapter<AccentAdapter.AccentAdapterSVH> {

    private LayoutInflater mInflater;
    private SingleClickListener mListener;
    private List<AccentsModel> mAccentsList;
    private int mSelectedAccentId;

    public AccentAdapter(List<AccentsModel> list, LayoutInflater inflater, int selectedAccentId, SingleClickListener listener) {
        this.mAccentsList = list;
        this.mInflater = inflater;
        this.mSelectedAccentId = selectedAccentId;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AccentAdapterSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccentAdapterSVH(mInflater.inflate(R.layout.rv_accent_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AccentAdapterSVH holder, int position) {
        holder.setData(mAccentsList.get(position), mSelectedAccentId);
    }

    @Override
    public int getItemCount() {
        return null == mAccentsList ? 0 : mAccentsList.size();
    }

    static class AccentAdapterSVH extends RecyclerView.ViewHolder {

        private ColorView mColorView;
        private MaterialTextView mTitle;

        AccentAdapterSVH(@NonNull View itemView, SingleClickListener listener) {
            super(itemView);
            mColorView = itemView.findViewById(R.id.accent_color);
            mTitle = itemView.findViewById(R.id.accent_title);
            itemView.setOnClickListener(v -> {
                listener.onItemCLick(getAdapterPosition());
                itemView.findViewById(R.id.accent_item_root).setBackground(itemView.getContext().getDrawable(R.drawable.selected_accent_item_background));
            });
        }

        void setData(AccentsModel accentsModel, int selectedAccentId) {
            if (accentsModel.getId() == selectedAccentId)
                itemView.findViewById(R.id.accent_item_root).setBackground(itemView.getContext().getDrawable(R.drawable.selected_accent_item_background));
            mColorView.setBackgroundColor(accentsModel.getColor());
            mTitle.setText(accentsModel.getTitle());
        }
    }
}
