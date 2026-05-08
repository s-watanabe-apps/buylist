package com.swapps.buylist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swapps.buylist.dto.ChildItem;
import com.swapps.buylist.dto.ParentItem;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder> {

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnItemActionListener {
        void onSelect(int position);
        void onEdit(int position);
        void onDelete(int position);
        void onCheck(int position);
    }

    private List<ChildItem> items;
    private OnStartDragListener dragListener;
    private OnItemActionListener actionListener;

    public SubAdapter(List<ChildItem> items,
                      OnStartDragListener dragListener,
                      OnItemActionListener actionListener) {
        this.items = items;
        this.dragListener = dragListener;
        this.actionListener = actionListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout area;
        TextView textName;
        TextView textValue;
        ImageView handle;
        ImageView delete;

        ViewHolder(View view) {
            super(view);
            area = view.findViewById(R.id.sub_item_area);
            textName = view.findViewById(R.id.sub_item_name);
            textValue = view.findViewById(R.id.sub_item_value);
            handle = view.findViewById(R.id.sub_drag_handle);
            delete = view.findViewById(R.id.sub_btn_delete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_sub_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Locale locale = Locale.getDefault();
        NumberFormat nf;
        if (Locale.JAPAN.equals(locale) || Locale.JAPANESE.equals(locale)) {
            nf = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        } else {
            nf = NumberFormat.getCurrencyInstance(locale);
        }

        holder.textName.setText(items.get(position).getName());
        holder.textValue.setText(nf.format(items.get(position).getValue()));

        if (items.get(position).getCheck()) {
            holder.handle.setImageResource(R.drawable.outline_check_box_24);
            holder.textName.setPaintFlags(
                    holder.textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            holder.handle.setImageResource(R.drawable.outline_check_box_outline_blank_24);
            holder.textName.setPaintFlags(
                    holder.textName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
        }

        // ドラッグ
        holder.handle.setOnLongClickListener(v -> {
            dragListener.onStartDrag(holder);
            return true;
        });

        // チェックボックス
        holder.handle.setOnClickListener(v -> {
            actionListener.onCheck(holder.getAdapterPosition());
        });

        // 選択
        holder.area.setOnClickListener(v -> {
            actionListener.onSelect(holder.getAdapterPosition());
        });

        // 編集
        holder.area.setOnLongClickListener(v -> {
            actionListener.onEdit(holder.getAdapterPosition());
            return true;
        });

        // 削除
        holder.delete.setOnClickListener(v -> {
            actionListener.onDelete(holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void swap(int from, int to) {
        Collections.swap(items, from, to);
        notifyItemMoved(from, to);
    }

    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public List<ChildItem> getItems() {
        return items;
    }
}

