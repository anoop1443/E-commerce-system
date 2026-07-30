package com.example.homeadmin.ui.orders;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Aapko OrderModel.java class banani hogi
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderModel> orderList;
    private Context context;
    private FirebaseFirestore db;

    public OrderAdapter(List<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Yahan order_item_layout.xml ko inflate karenge
        View view = LayoutInflater.from(context).inflate(R.layout.order_itme_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel currentOrder = orderList.get(position);

        // XML views mein data set karna
        holder.orderIdTv.setText("Order ID: #" + currentOrder.getOrderID());
        holder.userNameTv.setText("Customer: " + currentOrder.getFullName());
        holder.orderPriceTv.setText("Total: ₹" + currentOrder.getTotalAmount());
        holder.orderStatusTv.setText(currentOrder.getGlobalStatus());
       // holder.orderStatusTv.setText(currentOrder.getGlobalStatus().toUpperCase());

        // Delivery boy ka status check karna
        if (currentOrder.getDeliveryBoyName() != null && !currentOrder.getDeliveryBoyName().isEmpty()) {
            holder.deliveryBoyTv.setText("Assigned to: " + currentOrder.getDeliveryBoyName());
            holder.deliveryBoyTv.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.declinedInfoTv.setVisibility(View.GONE);
        } else {
            holder.deliveryBoyTv.setText("Assigned to: Not Assigned");
            holder.deliveryBoyTv.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            
            // Show decline info if available
            if (currentOrder.getLastDeclinedByBoyName() != null) {
                holder.declinedInfoTv.setVisibility(View.VISIBLE);
                holder.declinedInfoTv.setText("Declined by: " + currentOrder.getLastDeclinedByBoyName() + 
                        " (" + currentOrder.getDeclineReason() + ")");
            } else {
                holder.declinedInfoTv.setVisibility(View.GONE);
            }
        }
        // --- YAHAN CLICK LISTENER ADD KAREIN ---
        holder.itemView.setOnClickListener(v -> {
            // Context check karein
            if (context == null) {
                return;
            }

            // Nayi activity ko shuru karne ke liye Intent banayein
            Intent intent = new Intent(context, OrderDetailsActivity.class);

            // Intent ke saath Order ID bhejein
            intent.putExtra("ORDER_ID", currentOrder.getOrderID());

            // Activity shuru karein
            context.startActivity(intent);
        });



        // --- Button Click Listeners ---

        // 1. Order Status Update karne ke liye
        holder.updateStatusBtn.setOnClickListener(v -> {
            showStatusUpdateDialog(currentOrder);
        });

        // 2. Delivery Boy Assign karne ke liye
        holder.assignDeliveryBtn.setOnClickListener(v -> {
            showDeliveryBoyDialog(currentOrder);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    // --- Helper Methods for Dialogs ---

    private void showStatusUpdateDialog(OrderModel order) {
        String[] statusOptions = {"Ordered", "Processing", "Out for Service", "Completed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Order Status");
        builder.setItems(statusOptions, (dialog, which) -> {
            String selectedStatus = statusOptions[which];
            updateFirebaseOrder(order.getOrderID(), "globalStatus", selectedStatus);
        });
        builder.show();
    }

    private void showDeliveryBoyDialog(OrderModel order) {
        // Step 1: Delivery Boys ko Firebase se fetch karo
        db.collection("delivery_boy").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                List<String> boyNames = new ArrayList<>();
                List<String> boyIds = new ArrayList<>();

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    // Maan lo 'name' field hai delivery boy ke document mein
                    boyNames.add(doc.getString("name"));
                    boyIds.add(doc.getId());
                }

                // Step 2: Dialog mein list dikhao
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Assign Electrician Boy");
                builder.setItems(boyNames.toArray(new String[0]), (dialog, which) -> {
                    String selectedBoyName = boyNames.get(which);
                    String selectedBoyId = boyIds.get(which);

                    // Step 3: Order document ko update karo
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("deliveryBoyName", selectedBoyName);
                    updateData.put("deliveryBoyID", selectedBoyId);
                    updateData.put("lastDeclinedByBoyID", null);
                    updateData.put("lastDeclinedByBoyName", null);
                    updateData.put("declineReason", null);

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

    private void updateFirebaseOrder(String orderId, String key, Object value) {
        db.collection("ORDERS").document(orderId)
                .update(key, value)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Status Updated!", Toast.LENGTH_SHORT).show();
                    // UI automatically update ho jayega addSnapshotListener ki wajah se
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show());
    }


    // --- ViewHolder Class ---
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTv, orderStatusTv, userNameTv, orderPriceTv, deliveryBoyTv, declinedInfoTv;
        Button updateStatusBtn, assignDeliveryBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.order_id_tv);
            orderStatusTv = itemView.findViewById(R.id.order_status_tv);
            userNameTv = itemView.findViewById(R.id.user_name_tv);
            orderPriceTv = itemView.findViewById(R.id.order_price_tv);
            deliveryBoyTv = itemView.findViewById(R.id.delivery_boy_tv);
            declinedInfoTv = itemView.findViewById(R.id.declined_info_tv);
            updateStatusBtn = itemView.findViewById(R.id.update_status_btn);
            assignDeliveryBtn = itemView.findViewById(R.id.assign_delivery_btn);
        }
    }
}
