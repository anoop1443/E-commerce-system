package com.example.deliveryboy.earning;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliveryboy.R;
import com.example.deliveryboy.order.OrderDetailsActivity;

import java.util.List;
import java.util.Locale;

public class EarningAdapter extends RecyclerView.Adapter<EarningAdapter.ViewHolder> {
    private List<EarningRecord> earningList;

    public EarningAdapter(List<EarningRecord> earningList) {
        this.earningList = earningList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EarningRecord record = earningList.get(position);
        
        double amountValue = record.getAmount();
        String statusText = record.getStatus() != null ? record.getStatus() : "Delivered";
        
        if ("Cancelled".equalsIgnoreCase(statusText)) {
            holder.amount.setText("+ ₹" + String.format(Locale.getDefault(), "%.2f", amountValue));
            holder.amount.setTextColor(0xFFD32F2F); // Red for cancelled/visiting charge
            holder.orderId.setText("Order: " + record.getOrderId() + " (Cancelled)");
        } else {
            holder.amount.setText("+ ₹" + String.format(Locale.getDefault(), "%.2f", amountValue));
            holder.amount.setTextColor(0xFF2E7D32); // Green for delivered
            holder.orderId.setText("Order: " + record.getOrderId());
        }

        holder.date.setText(record.getFormattedDate());

        // Handle Click to open OrderDetails
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderDetailsActivity.class);
            intent.putExtra("ORDER_ID", record.getOrderId());
            intent.putExtra("IS_QUICK_ORDER", record.isQuickOrder());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return earningList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, orderId, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.earningAmount);
            orderId = itemView.findViewById(R.id.earningOrderId);
            date = itemView.findViewById(R.id.earningDate);
        }
    }
}
