package com.example.global.viewholder;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.global.databinding.ItemRowBinding;
import com.example.global.interfaces.fragmentListener;

import org.jetbrains.annotations.NotNull;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public ItemRowBinding itemRowBinding;

    public RecyclerViewHolder(@NonNull @NotNull ItemRowBinding itemRowBinding, fragmentListener fragmentListener) {
        super(itemRowBinding.getRoot());
        this.itemRowBinding = itemRowBinding;

        itemRowBinding.clBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentListener.onClickListener(itemRowBinding.getMItem().getIId());
            }
        });
    }
}
