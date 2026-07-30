package com.example.deliveryboy.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deliveryboy.R;
import com.example.deliveryboy.orderfech.Order;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderTitleTextView;
        private TextView orderStatusTextView;
        private ImageView orderImageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTitleTextView = itemView.findViewById(R.id.textViewHistoryTitle);
            orderStatusTextView = itemView.findViewById(R.id.textViewHistoryStatus);
            orderImageView = itemView.findViewById(R.id.imageViewHistory);
        }

        public void bind(Order order) {
            orderTitleTextView.setText("Order: " + order.getProductTitle());
            orderStatusTextView.setText("Status: " + order.getStatus());

            if (order.getImageUrl() != null && !order.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(order.getImageUrl())
                        .into(orderImageView);
            } else {
                orderImageView.setImageResource(R.drawable.ic_launcher_background); // Placeholder image
            }
        }
    }
}
