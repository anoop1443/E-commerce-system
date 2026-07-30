package com.example.homeelecation.ui.orders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeelecation.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class QuickOrderAdapter extends RecyclerView.Adapter<QuickOrderAdapter.ViewHolder> {

    private List<QuickOrderModel> quickOrderModelList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());

    public QuickOrderAdapter(List<QuickOrderModel> quickOrderModelList) {
        this.quickOrderModelList = quickOrderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickOrderModel model = quickOrderModelList.get(position);
        holder.setData(model);
    }

    @Override
    public int getItemCount() {
        return quickOrderModelList.size();
    }

    public void updateList(List<QuickOrderModel> newList) {
        this.quickOrderModelList = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderId, status, serviceName, price, date, address;
        private android.widget.ImageView serviceImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.quick_order_id);
            status = itemView.findViewById(R.id.quick_order_status);
            serviceName = itemView.findViewById(R.id.quick_service_name);
            price = itemView.findViewById(R.id.quick_order_price);
            date = itemView.findViewById(R.id.quick_order_date);
            address = itemView.findViewById(R.id.quick_order_address);
            serviceImage = itemView.findViewById(R.id.quick_order_image);
        }

        private void setData(QuickOrderModel model) {
            orderId.setText("Order ID: #" + model.getOrderId());
            status.setText(model.getOrderStatus());
            serviceName.setText(model.getServiceName());
            price.setText("₹" + model.getPrice());
            address.setText("Address: " + model.getUserAddress());

            if (model.getServiceImage() != null && !model.getServiceImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(model.getServiceImage())
                        .placeholder(R.drawable.tebal_fan)
                        .into(serviceImage);
            }

            if (model.getDateTime() != null) {
                date.setText("Date: " + dateFormat.format(model.getDateTime()));
            } else {
                date.setText("Date: N/A");
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), QuickOrderDetailsActivity.class);
                intent.putExtra("ORDER_ID", model.getOrderId());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
