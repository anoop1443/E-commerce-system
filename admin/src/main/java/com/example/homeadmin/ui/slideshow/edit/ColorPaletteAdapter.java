package com.example.homeadmin.ui.slideshow.edit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;

import java.util.List;

public class ColorPaletteAdapter extends RecyclerView.Adapter<ColorPaletteAdapter.ViewHolder> {

    private final List<Integer> colors;
    private final OnColorSelectedListener listener;

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public ColorPaletteAdapter(List<Integer> colors, OnColorSelectedListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_palette_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = colors.get(position);
        DrawableCompat.setTint(
                DrawableCompat.wrap(holder.colorSwatch.getBackground()),
                color
        );

        holder.itemView.setOnClickListener(v -> listener.onColorSelected(color));
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorSwatch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorSwatch = itemView.findViewById(R.id.color_swatch);
        }
    }
}