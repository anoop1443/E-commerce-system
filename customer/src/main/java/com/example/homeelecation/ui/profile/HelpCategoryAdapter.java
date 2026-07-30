package com.example.homeelecation.ui.profile;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeelecation.R;

import java.util.List;

public class HelpCategoryAdapter extends RecyclerView.Adapter<HelpCategoryAdapter.ViewHolder> {

    private List<HelpCategoryModel> categoryList;

    public HelpCategoryAdapter(List<HelpCategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpCategoryModel model = categoryList.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.category_name);
            layout = itemView.findViewById(R.id.category_layout);
        }

        public void bind(HelpCategoryModel model) {
            name.setText(model.getName());
            
            if (model.getColor() != null && !model.getColor().isEmpty()) {
                layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(model.getColor())));
            }

            Glide.with(itemView.getContext())
                    .load(model.getIcon())
                    .placeholder(R.drawable.ic_help)
                    .into(icon);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), HelpQuestionActivity.class);
                intent.putExtra("CATEGORY_ID", model.getCategoryId());
                intent.putExtra("CATEGORY_NAME", model.getName());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
