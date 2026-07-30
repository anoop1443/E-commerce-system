package com.example.homeadmin.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;

import java.util.List;
import java.util.Map;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final List<Map<String, Object>> orders;

    public OrdersAdapter(List<Map<String, Object>> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> order = orders.get(position);

        String orderId = (String) order.get("documentId");
        holder.orderIdTextView.setText("Order ID: " + orderId);
        holder.orderDateTextView.setText("Date: " + order.get("ordered_date"));
        holder.orderAmountTextView.setText("Amount: " + order.get("Total amount"));
        holder.orderStatusTextView.setText("Status: " + order.get("globalStatus"));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminOrderDetailActivity.class);
            intent.putExtra("order_id", orderId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderDateTextView, orderAmountTextView, orderStatusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id_text_view);
            orderDateTextView = itemView.findViewById(R.id.order_date_text_view);
            orderAmountTextView = itemView.findViewById(R.id.order_amount_text_view);
            orderStatusTextView = itemView.findViewById(R.id.order_status_text_view);
        }
    }
}
