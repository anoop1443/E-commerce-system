package com.example.homeelecation.ui.quickOrder;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.R;

import java.util.List;

public class QuickServiceAdapter extends RecyclerView.Adapter<QuickServiceAdapter.ViewHolder> {

    private List<QuickServiceModel> serviceList;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(QuickServiceModel model);
    }

    public QuickServiceAdapter(List<QuickServiceModel> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_quick_service_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(serviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private TextView name, price;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.quick_service_container);
            name = itemView.findViewById(R.id.quick_service_name);
            price = itemView.findViewById(R.id.quick_service_price);
            icon = itemView.findViewById(R.id.quick_service_icon);
        }

        private void setData(QuickServiceModel model) {
            name.setText(model.getName());

            Glide.with(itemView.getContext()).load(model.getIcon())
                    .apply(new RequestOptions().placeholder(R.drawable.ic_cart))
                    .into(icon);

            if (model.isAvailable()) {
                price.setText("₹" + model.getPrice());
                itemView.setAlpha(1.0f);
                itemView.setOnClickListener(v -> listener.onServiceClick(model));
            } else {
                price.setText("Not Available");
                itemView.setAlpha(0.6f);
                itemView.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "This service is currently unavailable", Toast.LENGTH_SHORT).show());
            }
            
            try {
                container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(model.getColor())));
            } catch (Exception e) {
                container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
            }
        }
    }
}
