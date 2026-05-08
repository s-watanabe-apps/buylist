package com.swapps.buylist;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MainDragCallback extends ItemTouchHelper.Callback {

    public interface OnDragFinishedListener {
        void onDragFinished();
    }

    private final OnDragFinishedListener listener;

    private final MainAdapter adapter;

    public MainDragCallback(MainAdapter adapter, OnDragFinishedListener listener) {
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false; // ハンドル限定
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {

        adapter.swap(
                viewHolder.getAdapterPosition(),
                target.getAdapterPosition()
        );
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // 未使用
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder.itemView.setAlpha(0.7f);
            viewHolder.itemView.setScaleX(1.05f);
            viewHolder.itemView.setScaleY(1.05f);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(1f);
        viewHolder.itemView.setScaleX(1f);
        viewHolder.itemView.setScaleY(1f);

        if (listener != null) {
            listener.onDragFinished();
        }
    }
}