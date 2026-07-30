package com.example.deliveryboy.order;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryboy.R;

import java.util.List;

public class OrderAdapterD extends RecyclerView.Adapter<OrderAdapterD.OrderViewHolder> {

    private List<OrderModel> orderList;

    public OrderAdapterD(List<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_d, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, customerNameTextView, customerAddressTextView, orderStatusTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.order_id);
            customerNameTextView = itemView.findViewById(R.id.customer_name);
            customerAddressTextView = itemView.findViewById(R.id.customer_address);
            orderStatusTextView = itemView.findViewById(R.id.order_status);


        }



        public void bind(OrderModel order) {
            orderIdTextView.setText("Order ID:  " + order.getOrderId());
            customerNameTextView.setText("Customer: " + order.getCustomerName());
            customerAddressTextView.setText("Address: " + order.getCustomerAddress());
            orderStatusTextView.setText("Status: " + order.getOrderStatus());

            // Item par click listener add karein
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // OrderDetailsActivity ko call karein aur orderId bhej dein
                    Intent intent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("ORDER_ID",order.getOrderId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
