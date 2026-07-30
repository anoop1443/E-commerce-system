package com.example.homeadmin.ui.categoryView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;

import java.util.ArrayList;
import java.util.List;

public class ManageCategoryAdapter extends RecyclerView.Adapter<ManageCategoryAdapter.ViewHolder> {

    private final List<CategoryModel> categoryList;
    private final List<CategoryModel> deletedItems = new ArrayList<>();
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEdit(CategoryModel category);
        void onDelete(CategoryModel category, int position);
    }

    public ManageCategoryAdapter(List<CategoryModel> categoryList, OnCategoryActionListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_home_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel model = categoryList.get(position);
        
        holder.nameText.setText(model.getCategoryName());
        holder.titleText.setVisibility(View.GONE); // Not needed for categories

        Glide.with(holder.itemView.getContext())
                .load(model.getCategoryIconLink())
                .placeholder(R.drawable.ic_home)
                .into(holder.iconImage);

        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(model, holder.getAdapterPosition());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            deletedItems.add(categoryList.get(position));
            categoryList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, titleText;
        ImageButton editBtn, deleteBtn;
        ImageView iconImage;
        View dragHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_type_textview);
            titleText = itemView.findViewById(R.id.item_title_textview);
            editBtn = itemView.findViewById(R.id.edit_icon);
            deleteBtn = itemView.findViewById(R.id.delete_icon);
            dragHandle = itemView.findViewById(R.id.drag_handle);
            
            // For categories, let's use the icon preview instead of just a dot handle
            iconImage = (ImageView) dragHandle; 
        }
    }
}
