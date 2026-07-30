package com.example.homeadmin.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private List<OrderModel> orderList;
    private Context context;
    private FirebaseFirestore db;

    public AdminOrderAdapter(List<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_itme_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel model = orderList.get(position);

        holder.orderId.setText("Order ID: #" + model.getOrderID());
        holder.customerName.setText("Customer: " + (model.getFullName() != null ? model.getFullName() : "N/A"));
        holder.totalAmount.setText("Total: ₹" + model.getTotalAmount());
        holder.status.setText(model.getGlobalStatus());

        if (model.getDeliveryBoyName() != null && !model.getDeliveryBoyName().isEmpty()) {
            holder.deliveryBoy.setText("Assigned to: " + model.getDeliveryBoyName());
            holder.deliveryBoy.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.deliveryBoy.setText("Assigned to: Not Assigned");
            holder.deliveryBoy.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("ORDER_ID", model.getOrderID());
            context.startActivity(intent);
        });

        holder.updateStatusBtn.setOnClickListener(v -> showStatusUpdateDialog(model));
        holder.assignDeliveryBtn.setOnClickListener(v -> showDeliveryBoyDialog(model));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void showStatusUpdateDialog(OrderModel order) {
        String[] statusOptions = {"Ordered", "Processing", "Out for Service", "Completed", "Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Order Status");
        builder.setItems(statusOptions, (dialog, which) -> {
            String selectedStatus = statusOptions[which];
            updateFirebaseOrder(order.getOrderID(), selectedStatus);
        });
        builder.show();
    }

    private void updateFirebaseOrder(String orderId, String newStatus) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("globalStatus", newStatus);

        // Add corresponding timestamp
        switch (newStatus) {
            case "Processing": updateMap.put("packedDate", FieldValue.serverTimestamp()); break;
            case "Out for Service": updateMap.put("shippedDate", FieldValue.serverTimestamp()); break;
            case "Completed": updateMap.put("deliveredDate", FieldValue.serverTimestamp()); break;
            case "Cancelled": updateMap.put("cancelledDate", FieldValue.serverTimestamp()); break;
        }

        // 1. Update Global Status
        db.collection("ORDERS").document(orderId)
                .update(updateMap)
                .addOnSuccessListener(aVoid -> {
                    // 2. Sync with Items
                    db.collection("ORDERS").document(orderId).collection("orderItems")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    String currentItemStatus = doc.getString("orderStatus");
                                    // SMART LOGIC: Skip already cancelled/completed items
                                    if (currentItemStatus != null && !currentItemStatus.equalsIgnoreCase("Cancelled") && !currentItemStatus.equalsIgnoreCase("Completed") && !currentItemStatus.equalsIgnoreCase("Delivered")) {
                                        doc.getReference().update(updateMap);
                                    }
                                }
                                Toast.makeText(context, "Status & Time Updated!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show());
    }

    private void showDeliveryBoyDialog(OrderModel order) {
        db.collection("delivery_boy").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                List<String> boyNames = new ArrayList<>();
                List<String> boyIds = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    boyNames.add(doc.getString("name"));
                    boyIds.add(doc.getId());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Assign Electrician Boy");
                builder.setItems(boyNames.toArray(new String[0]), (dialog, which) -> {
                    String selectedBoyName = boyNames.get(which);
                    String selectedBoyId = boyIds.get(which);
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("deliveryBoyName", selectedBoyName);
                    updateData.put("deliveryBoyID", selectedBoyId);
                    db.collection("ORDERS").document(order.getOrderID())
                            .update(updateData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned to " + selectedBoyName, Toast.LENGTH_SHORT).show());
                });
                builder.show();
            } else {
                Toast.makeText(context, "No Electrician Boys found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, status, customerName, totalAmount, deliveryBoy;
        Button updateStatusBtn, assignDeliveryBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id_tv);
            status = itemView.findViewById(R.id.order_status_tv);
            customerName = itemView.findViewById(R.id.user_name_tv);
            totalAmount = itemView.findViewById(R.id.order_price_tv);
            deliveryBoy = itemView.findViewById(R.id.delivery_boy_tv);
            updateStatusBtn = itemView.findViewById(R.id.update_status_btn);
            assignDeliveryBtn = itemView.findViewById(R.id.assign_delivery_btn);
        }
    }
}
