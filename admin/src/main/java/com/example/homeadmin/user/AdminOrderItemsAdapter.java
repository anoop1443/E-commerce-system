package com.example.homeadmin.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminOrderItemsAdapter extends RecyclerView.Adapter<AdminOrderItemsAdapter.ViewHolder> {

    private final List<OrderItem> orderItems;

    public AdminOrderItemsAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_item_details_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        Glide.with(holder.itemView.getContext()).load(item.getProductImage()).into(holder.productImage);
        holder.productTitle.setText(item.getProductTitle());
        holder.productPrice.setText("Price: ₹" + item.getProductPrice());
        holder.productQuantity.setText("Qty: " + item.getProductQuantity());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        if (item.getTime() != null) {
            holder.orderTime.setText("Time: " + timeFormat.format(item.getTime()));
            holder.orderTime.setVisibility(View.VISIBLE);
        } else {
            holder.orderTime.setVisibility(View.GONE);
        }

        if (item.getOrderedDate() != null) {
            holder.orderedDate.setText("Ordered: " + dateFormat.format(item.getOrderedDate()));
            holder.orderedDate.setVisibility(View.VISIBLE);
        } else {
            holder.orderedDate.setVisibility(View.GONE);
        }

        if (item.getPackedDate() != null) {
            holder.packedDate.setText("Packed: " + dateFormat.format(item.getPackedDate()));
            holder.packedDate.setVisibility(View.VISIBLE);
        } else {
            holder.packedDate.setVisibility(View.GONE);
        }

        if (item.getShippedDate() != null) {
            holder.shippedDate.setText("Shipped: " + dateFormat.format(item.getShippedDate()));
            holder.shippedDate.setVisibility(View.VISIBLE);
        } else {
            holder.shippedDate.setVisibility(View.GONE);
        }

        if (item.getDeliveredDate() != null) {
            holder.deliveredDate.setText("Delivered: " + dateFormat.format(item.getDeliveredDate()));
            holder.deliveredDate.setVisibility(View.VISIBLE);
        } else {
            holder.deliveredDate.setVisibility(View.GONE);
        }

        if (item.getCancelledDate() != null) {
            holder.cancelledDate.setText("Cancelled: " + dateFormat.format(item.getCancelledDate()));
            holder.cancelledDate.setVisibility(View.VISIBLE);
        } else {
            holder.cancelledDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice, productQuantity, orderTime, orderedDate, packedDate, shippedDate, deliveredDate, cancelledDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            orderTime = itemView.findViewById(R.id.order_time);
            orderedDate = itemView.findViewById(R.id.ordered_date);
            packedDate = itemView.findViewById(R.id.packed_date);
            shippedDate = itemView.findViewById(R.id.shipped_date);
            deliveredDate = itemView.findViewById(R.id.delivered_date);
            cancelledDate = itemView.findViewById(R.id.cancelled_date);
        }
    }
}
