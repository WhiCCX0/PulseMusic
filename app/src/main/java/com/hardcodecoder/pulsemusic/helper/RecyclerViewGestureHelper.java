package com.hardcodecoder.pulsemusic.helper;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;

import static com.google.android.material.color.MaterialColors.ALPHA_FULL;

public class RecyclerViewGestureHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    public RecyclerViewGestureHelper(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        mAdapter.onItemMoved(fromPos, toPos);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }


    @Override
    public void onChildDraw(
            @NonNull Canvas c,
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}
