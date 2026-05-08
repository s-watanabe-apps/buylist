package com.swapps.buylist;

import android.os.Build;
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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnItemActionListener {
        void onSelect(int position);
        void onEdit(int position);
        void onDelete(int position);
    }

    private List<ParentItem> list;
    private OnStartDragListener dragListener;
    private OnItemActionListener actionListener;

    public MainAdapter(List<ParentItem> list,
                           OnStartDragListener dragListener,
                           OnItemActionListener actionListener) {
        this.list = list;
        this.dragListener = dragListener;
        this.actionListener = actionListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout area;
        TextView textName;
        TextView textTotal;
        TextView textCreatedAt;
        ImageView handle;
        ImageView delete;

        ViewHolder(View view) {
            super(view);
            area = view.findViewById(R.id.main_item_area);
            textName = view.findViewById(R.id.main_item_name);
            textTotal = view.findViewById(R.id.main_item_total);
            textCreatedAt = view.findViewById(R.id.main_item_created_at);
            handle = view.findViewById(R.id.main_drag_handle);
            delete = view.findViewById(R.id.main_btn_delete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Locale locale = Locale.getDefault();
        SimpleDateFormat sdf;
        NumberFormat nf;
        if (Locale.JAPAN.equals(locale) || Locale.JAPANESE.equals(locale)) {
            sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN);
            nf = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        } else {
            sdf = new SimpleDateFormat("MMM d, yyyy", locale);
            nf = NumberFormat.getCurrencyInstance(locale);
        }

        holder.textName.setText(list.get(position).getName());
        holder.textTotal.setText(nf.format(list.get(position).getItems()
                .stream()
                .mapToInt(ChildItem::getValue)
                .sum()
        ));
        holder.textCreatedAt.setText(sdf.format(list.get(position).getCreatedAt()));

        // ドラッグ
        holder.handle.setOnLongClickListener(v -> {
            dragListener.onStartDrag(holder);
            return true;
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
        return list.size();
    }

    public void swap(int from, int to) {
        Collections.swap(list, from, to);
        notifyItemMoved(from, to);
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public List<ParentItem> getItems() {
        return list;
    }
}

