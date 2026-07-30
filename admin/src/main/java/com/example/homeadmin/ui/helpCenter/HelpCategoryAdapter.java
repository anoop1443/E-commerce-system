package com.example.homeadmin.ui.helpCenter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;

import java.util.List;

public class HelpCategoryAdapter extends RecyclerView.Adapter<HelpCategoryAdapter.ViewHolder> {

    private List<HelpCategoryModel> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(HelpCategoryModel category);
        void onEditClick(HelpCategoryModel category);
        void onDeleteClick(HelpCategoryModel category);
    }

    public HelpCategoryAdapter(List<HelpCategoryModel> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpCategoryModel category = categoryList.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView nameView;
        View editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.category_icon);
            nameView = itemView.findViewById(R.id.category_name);
            editBtn = itemView.findViewById(R.id.btn_edit_category);
            deleteBtn = itemView.findViewById(R.id.btn_delete_category);
        }

        public void bind(final HelpCategoryModel category, final OnCategoryClickListener listener) {
            nameView.setText(category.getName());
            
            Glide.with(itemView.getContext())
                    .load(category.getIcon())
                    .placeholder(R.drawable.ic_home)
                    .into(iconView);

            try {
                int color = Color.parseColor(category.getColor());
                iconView.setBackgroundTintList(ColorStateList.valueOf(color));
            } catch (Exception e) {
                iconView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
            }

            itemView.setOnClickListener(v -> listener.onCategoryClick(category));
            editBtn.setOnClickListener(v -> listener.onEditClick(category));
            deleteBtn.setOnClickListener(v -> listener.onDeleteClick(category));
        }
    }
}
