package com.example.homeadmin.ui.quickServices;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class QuickServiceManageAdapter extends RecyclerView.Adapter<QuickServiceManageAdapter.ViewHolder> {

    private List<QuickServiceModel> serviceList;
    private Context context;
    private FirebaseFirestore db;

    public QuickServiceManageAdapter(List<QuickServiceModel> serviceList) {
        this.serviceList = serviceList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_quick_service_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickServiceModel model = serviceList.get(position);
        holder.setData(model);

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddQuickServiceActivity.class);
            intent.putExtra("SERVICE_ID", model.getId());
            context.startActivity(intent);
        });

        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteService(model, position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void deleteService(QuickServiceModel model, int position) {
        com.example.homeadmin.ui.trash.TrashManager.moveToTrash(
                "QUICK_SERVICES",
                model.getId(),
                "QUICK_SERVICE",
                model.getName(),
                model.getIcon(),
                new com.example.homeadmin.ui.trash.TrashManager.OnTrashOperationListener() {
                    @Override
                    public void onSuccess() {
                        serviceList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, serviceList.size());
                        Toast.makeText(context, "Moved to Recycle Bin", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name, price, status, category, desc, indexTv;
        private View colorPreview;
        private ImageButton editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.service_icon);
            name = itemView.findViewById(R.id.service_name);
            price = itemView.findViewById(R.id.service_price);
            status = itemView.findViewById(R.id.service_status);
            category = itemView.findViewById(R.id.service_category);
            desc = itemView.findViewById(R.id.service_desc);
            indexTv = itemView.findViewById(R.id.service_index);
            colorPreview = itemView.findViewById(R.id.color_preview);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        public void setData(QuickServiceModel model) {
            name.setText(model.getName());
            price.setText("Price: ₹" + model.getPrice());
            category.setText(model.getCategory() != null ? model.getCategory() : "No Category");
            desc.setText(model.getDescription() != null ? model.getDescription() : "No Description");
            indexTv.setText(" | Index: " + model.getIndex());

            status.setText(model.isAvailable() ? "Available" : "Not Available");
            status.setTextColor(itemView.getContext().getResources().getColor(
                    model.isAvailable() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

            if (model.getColor() != null && !model.getColor().isEmpty()) {
                try {
                    colorPreview.setBackgroundColor(android.graphics.Color.parseColor(model.getColor()));
                } catch (Exception e) {
                    colorPreview.setBackgroundColor(android.graphics.Color.BLACK);
                }
            }

            if (model.getIcon() != null && !model.getIcon().isEmpty()) {
                Glide.with(itemView.getContext()).load(model.getIcon()).placeholder(R.drawable.ic_home).into(icon);
            } else {
                icon.setImageResource(R.drawable.ic_home);
            }
        }
    }
}
