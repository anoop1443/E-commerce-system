package com.example.homeadmin.ui.orders;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Glide library ka istemal karein
import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<MyOrderItemModel> itemList;
    private Context context;
    private SimpleDateFormat sdf;

    private FirebaseFirestore db;

    public OrderDetailAdapter(List<MyOrderItemModel> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        // Date format karne ke liye
        this.sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyOrderItemModel item = itemList.get(position);

        holder.title.setText(item.getProductTitle());
        holder.priceQuantity.setText("Price: ₹" + item.getProductPrice() + " | Qty: " + item.getQuantity());
        holder.status.setText(item.getOrderStatus());
        String orderId = item.getOrderId();
        String productId = item.getProductId();

        // Image load karein
        Glide.with(context).load(item.getProductImage()).placeholder(R.drawable.tebal_fan).into(holder.image);

        // Display Assigned Boy
        if (item.getDeliveryBoyName() != null && !item.getDeliveryBoyName().isEmpty()) {
            holder.assignedBoy.setVisibility(View.VISIBLE);
            holder.assignedBoy.setText("Assigned to: " + item.getDeliveryBoyName());
        } else {
            holder.assignedBoy.setVisibility(View.GONE);
        }

        // Date ko format karein
        if (item.getDeliveredDate() != null) {
            String formattedDate = sdf.format(item.getDeliveredDate());
            holder.promiseDate.setText("Promised Delivery: " + formattedDate);
        } else {
            holder.promiseDate.setText("Promised Delivery: Not Available");
        }

        // 1. Order Status Update karne ke liye
        holder.updateStatusBtn.setOnClickListener(v -> {
            showStatusUpdateDialog(orderId,productId);
        });

        // 2. Assign Boy for individual item
        holder.assignBoyBtn.setOnClickListener(v -> {
            showDeliveryBoyDialog(orderId, productId);
        });

        // Refund Status display
        if (item.getRefundStatus() != null && !item.getRefundStatus().isEmpty()) {
            holder.refundStatus.setVisibility(View.VISIBLE);
            holder.refundStatus.setText("Refund: " + item.getRefundStatus());
            if ("Refunded".equals(item.getRefundStatus())) {
                holder.refundStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                holder.refundStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
            }
        } else if ("Cancelled".equals(item.getOrderStatus())) {
            holder.refundStatus.setVisibility(View.VISIBLE);
            holder.refundStatus.setText("Refund: Pending Action");
            holder.refundStatus.setTextColor(Color.RED);
        } else {
            holder.refundStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void showStatusUpdateDialog(String orderId,String productId) {
        String[] statusOptions = {"Ordered", "Processing", "Out for Service", "Completed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Order Status");
        builder.setItems(statusOptions, (dialog, which) -> {
            String selectedStatus = statusOptions[which];
            updateFirebaseOrder(orderId, productId,"orderStatus", selectedStatus);
        });
        builder.show();
    }

    private void updateFirebaseOrder(String orderId, String productID, String key, Object value) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);

        // Add corresponding timestamp if it's a status update
        if ("orderStatus".equals(key) && value instanceof String) {
            String status = (String) value;
            switch (status) {
                case "Processing": updates.put("packedDate", FieldValue.serverTimestamp()); break;
                case "Out for Service": updates.put("shippedDate", FieldValue.serverTimestamp()); break;
                case "Completed": updates.put("deliveredDate", FieldValue.serverTimestamp()); break;
                case "Cancelled": updates.put("cancelledDate", FieldValue.serverTimestamp()); break;
            }
        }

        // 1. Update Specific Item Status
        db.collection("ORDERS").document(orderId).collection("orderItems").document(productID)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item Updated with Time!", Toast.LENGTH_SHORT).show();

                    // 2. SMART LOGIC: If item became 'Completed', check if all items are done
                    if ("Completed".equals(value) || "Delivered".equals(value)) {
                        checkAndUpdateGlobalStatus(orderId);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show());
    }

    private void checkAndUpdateGlobalStatus(String orderId) {
        db.collection("ORDERS").document(orderId).collection("orderItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean allDelivered = true;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String status = doc.getString("orderStatus");
                        // If any item is NOT Completed/Delivered AND NOT Cancelled, then order is still pending
                        if (status != null && !status.equalsIgnoreCase("Completed") && !status.equalsIgnoreCase("Delivered") && !status.equalsIgnoreCase("Cancelled")) {
                            allDelivered = false;
                            break;
                        }
                    }

                    if (allDelivered) {
                        db.collection("ORDERS").document(orderId).update("globalStatus", "Completed");
                        Toast.makeText(context, "All items completed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showDeliveryBoyDialog(String orderId, String productId) {
        db.collection("delivery_boy").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                List<String> boyNames = new ArrayList<>();
                List<String> boyIds = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    boyNames.add(doc.getString("name"));
                    boyIds.add(doc.getId());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Assign Technician to Item");
                builder.setItems(boyNames.toArray(new String[0]), (dialog, which) -> {
                    String selectedBoyName = boyNames.get(which);
                    String selectedBoyId = boyIds.get(which);
                    
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("deliveryBoyName", selectedBoyName);
                    updateData.put("deliveryBoyID", selectedBoyId);
                    updateData.put("orderStatus", "Ordered"); // Reset to ordered if reassigned
                    
                    db.collection("ORDERS").document(orderId)
                            .collection("orderItems").document(productId)
                            .update(updateData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Technician assigned to item!", Toast.LENGTH_SHORT).show());
                });
                builder.show();
            } else {
                Toast.makeText(context, "No Technicians found!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView  status, title, priceQuantity, promiseDate, refundStatus, assignedBoy;
        Button updateStatusBtn, assignBoyBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            status = itemView.findViewById(R.id.item_Status);
            title = itemView.findViewById(R.id.item_title);
            priceQuantity = itemView.findViewById(R.id.item_price_quantity);
            promiseDate = itemView.findViewById(R.id.item_promise_date);
            refundStatus = itemView.findViewById(R.id.item_refund_status);
            assignedBoy = itemView.findViewById(R.id.item_assigned_boy);
            updateStatusBtn = itemView.findViewById(R.id.update_status_btn_orderDetail);
            assignBoyBtn = itemView.findViewById(R.id.assign_boy_btn_orderDetail);
        }
    }
}

