package com.example.global.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.global.classes.Item;
import com.example.global.databinding.ItemRowBinding;
import com.example.global.interfaces.fragmentListener;
import com.example.global.viewholder.RecyclerViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    List<Item> items;
    fragmentListener fragmentListener;

    public RecyclerAdapter(List<Item> items, fragmentListener fragmentListener) {
        this.items = items;
        this.fragmentListener = fragmentListener;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRowBinding itemRowBinding = ItemRowBinding.inflate(inflater, parent, false);
        return new RecyclerViewHolder(itemRowBinding, fragmentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewHolder holder, int position) {
        Item i = items.get(position);
        holder.itemRowBinding.setMItem(i);
        holder.itemRowBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
