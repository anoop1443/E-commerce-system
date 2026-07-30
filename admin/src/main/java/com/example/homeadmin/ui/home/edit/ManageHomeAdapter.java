package com.example.homeadmin.ui.home.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.home2.Home3Model;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.slideshow.SliderModel;

import java.util.ArrayList;
import java.util.List;

public class ManageHomeAdapter extends RecyclerView.Adapter<ManageHomeAdapter.ViewHolder> {

    private final List<Home3Model> home3Modellist;
    private final List<Home3Model> deletedItems = new ArrayList<>();
    private OnItemEditListener onItemEditListener;

    public interface OnItemEditListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onAddContentClick(int position);
    }

    public void setOnItemEditListener(OnItemEditListener listener) {
        this.onItemEditListener = listener;
    }

    public ManageHomeAdapter(List<Home3Model> home3Models, android.content.Context context) {
        this.home3Modellist = home3Models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_home_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Home3Model model = home3Modellist.get(position);
        
        String typeLabel;
        int iconRes;
        boolean showTitle = false;
        boolean showColor = false;
        
        switch (model.getType()) {
            case Home3Model.BANNER_SLIDER:
                typeLabel = "Banner Slider";
                iconRes = R.drawable.ic_menu_gallery;
                break;
            case Home3Model.STRIP_AD_BANNER:
                typeLabel = "Strip Ad Banner";
                iconRes = R.drawable.ic_menu_slideshow;
                break;
            case Home3Model.HORIZONTAL_PRODUCT:
                typeLabel = "Horizontal Product List";
                iconRes = R.drawable.ic_baseline_settings_24;
                showTitle = true;
                showColor = true;
                break;
            case Home3Model.GRID_PRODUCT_VIEW:
                typeLabel = "Grid Product List";
                iconRes = R.drawable.ic_dot;
                showTitle = true;
                showColor = true;
                break;
            default:
                typeLabel = "Unknown Type (" + model.getType() + ")";
                iconRes = R.drawable.ic_baseline_settings_24;
                break;
        }

        holder.typeText.setText(typeLabel);
        holder.typeIcon.setImageResource(iconRes);
        
        if (showTitle && model.getTitel() != null && !model.getTitel().isEmpty()) {
            holder.titleText.setText(model.getTitel());
            holder.titleText.setVisibility(View.VISIBLE);
        } else {
            holder.titleText.setVisibility(View.GONE);
        }

        if (showColor && model.getBackgoundcolor() != null && !model.getBackgoundcolor().isEmpty()) {
            try {
                holder.colorIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(model.getBackgoundcolor())));
                holder.colorIndicator.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.colorIndicator.setVisibility(View.GONE);
            }
        } else {
            holder.colorIndicator.setVisibility(View.GONE);
        }

        // Setup Content Preview
        List<String> imageUrls = new ArrayList<>();
        if (model.getType() == Home3Model.BANNER_SLIDER && model.getSliderModelList() != null) {
            for (SliderModel sm : model.getSliderModelList()) {
                if (sm.getBanner() != null) imageUrls.add(sm.getBanner());
            }
        } else if (model.getType() == Home3Model.STRIP_AD_BANNER && model.getStripImage() != null) {
            imageUrls.add(model.getStripImage());
        } else if ((model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) && model.getHorizontalproductscrollModelList() != null) {
            for (HorizontalProductScrollModel pm : model.getHorizontalproductscrollModelList()) {
                if (pm.getProductImage() != null) imageUrls.add(pm.getProductImage());
            }
        }

        if (!imageUrls.isEmpty()) {
            holder.contentPreviewRv.setVisibility(View.VISIBLE);
            MiniPreviewAdapter miniAdapter = new MiniPreviewAdapter(imageUrls, pos -> {
                // Remove individual item logic
                if (model.getContentIds() != null && pos < model.getContentIds().size()) {
                    model.getContentIds().remove(pos);
                    if (model.getSliderModelList() != null && pos < model.getSliderModelList().size()) model.getSliderModelList().remove(pos);
                    if (model.getHorizontalproductscrollModelList() != null && pos < model.getHorizontalproductscrollModelList().size()) model.getHorizontalproductscrollModelList().remove(pos);
                    notifyItemChanged(holder.getAdapterPosition());
                } else if (model.getType() == Home3Model.STRIP_AD_BANNER) {
                    model.setAdId(null);
                    model.setStripImage(null);
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });
            holder.contentPreviewRv.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.contentPreviewRv.setAdapter(miniAdapter);
        } else {
            holder.contentPreviewRv.setVisibility(View.GONE);
        }

        holder.deleteBtn.setOnClickListener(v -> {
            if (onItemEditListener != null) {
                onItemEditListener.onDeleteClick(holder.getAdapterPosition());
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            if (onItemEditListener != null) {
                onItemEditListener.onEditClick(holder.getAdapterPosition());
            }
        });

        holder.addContentBtn.setOnClickListener(v -> {
            if (onItemEditListener != null) {
                onItemEditListener.onAddContentClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return home3Modellist.size();
    }

    public void deleteItem(int position) {
        if (position != RecyclerView.NO_POSITION) {
            deletedItems.add(home3Modellist.get(position));
            home3Modellist.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<Home3Model> getDeletedItems() {
        return deletedItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, titleText;
        ImageButton editBtn, deleteBtn, addContentBtn;
        ImageView typeIcon;
        View dragHandle, colorIndicator;
        RecyclerView contentPreviewRv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.item_type_textview);
            titleText = itemView.findViewById(R.id.item_title_textview);
            editBtn = itemView.findViewById(R.id.edit_icon);
            deleteBtn = itemView.findViewById(R.id.delete_icon);
            addContentBtn = itemView.findViewById(R.id.add_content_icon);
            dragHandle = itemView.findViewById(R.id.drag_handle);
            typeIcon = itemView.findViewById(R.id.type_icon);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
            contentPreviewRv = itemView.findViewById(R.id.content_preview_recyclerview);
        }
    }

    private static class MiniPreviewAdapter extends RecyclerView.Adapter<MiniPreviewAdapter.MiniViewHolder> {
        private final List<String> images;
        private final OnRemoveClickListener removeListener;

        interface OnRemoveClickListener {
            void onRemoveClick(int position);
        }

        MiniPreviewAdapter(List<String> images, OnRemoveClickListener removeListener) {
            this.images = images;
            this.removeListener = removeListener;
        }

        @NonNull
        @Override
        public MiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_preview_item, parent, false);
            return new MiniViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MiniViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(images.get(position)).placeholder(R.drawable.ic_home).into(holder.img);
            holder.removeBtn.setOnClickListener(v -> removeListener.onRemoveClick(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        static class MiniViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            ImageButton removeBtn;

            MiniViewHolder(@NonNull View v) {
                super(v);
                img = v.findViewById(R.id.preview_image);
                removeBtn = v.findViewById(R.id.remove_item_btn);
            }
        }
    }
}