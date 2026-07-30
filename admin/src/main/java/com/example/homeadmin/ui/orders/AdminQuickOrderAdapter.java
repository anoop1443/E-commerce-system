package com.example.homeadmin.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminQuickOrderAdapter extends RecyclerView.Adapter<AdminQuickOrderAdapter.ViewHolder> {

    private List<QuickOrderModel> quickOrderList;
    private FirebaseFirestore db;
    private Context context;

    public AdminQuickOrderAdapter(List<QuickOrderModel> quickOrderList) {
        this.quickOrderList = quickOrderList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.admin_quick_order_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickOrderModel model = quickOrderList.get(position);
        holder.setData(model);

        holder.updateStatusBtn.setOnClickListener(v -> showStatusUpdateDialog(model));
        holder.assignDeliveryBtn.setOnClickListener(v -> showDeliveryBoyDialog(model));
        holder.callBtn.setOnClickListener(v -> {
            if (model.getUserMobile() != null && !model.getUserMobile().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + model.getUserMobile()));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Mobile number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return quickOrderList.size();
    }

    private void showStatusUpdateDialog(QuickOrderModel order) {
        String[] statusOptions = {"Ordered", "Assigned", "Completed", "Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Status");
        builder.setItems(statusOptions, (dialog, which) -> {
            String selectedStatus = statusOptions[which];
            Map<String, Object> updates = new HashMap<>();
            updates.put("orderStatus", selectedStatus);

            // Add corresponding timestamp
            if ("Completed".equalsIgnoreCase(selectedStatus)) {
                updates.put("deliveredDate", FieldValue.serverTimestamp());
            } else if ("Assigned".equalsIgnoreCase(selectedStatus)) {
                updates.put("shippedDate", FieldValue.serverTimestamp());
            } else if ("Cancelled".equalsIgnoreCase(selectedStatus)) {
                updates.put("cancelledDate", FieldValue.serverTimestamp());
            }

            db.collection("ORDERS_QUICK").document(order.getOrderId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Status & Time Updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show());
        });
        builder.show();
    }

    private void showDeliveryBoyDialog(QuickOrderModel order) {
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
                    updateData.put("orderStatus","Assigned");
                    updateData.put("deliveryBoyID", selectedBoyId);
                    updateData.put("lastDeclinedByBoyID", null);
                    updateData.put("lastDeclinedByBoyName", null);
                    updateData.put("declineReason", null);
                    db.collection("ORDERS_QUICK").document(order.getOrderId())
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
        private TextView orderId, status, serviceName, userName, userPhone, price, date, address, deliveryBoy, declinedInfo;
        private Button updateStatusBtn, assignDeliveryBtn, callBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.quick_order_id);
            status = itemView.findViewById(R.id.quick_order_status);
            serviceName = itemView.findViewById(R.id.quick_service_name);
            userName = itemView.findViewById(R.id.quick_user_name);
            userPhone = itemView.findViewById(R.id.quick_user_phone);
            price = itemView.findViewById(R.id.quick_order_price);
            date = itemView.findViewById(R.id.quick_order_date);
            address = itemView.findViewById(R.id.quick_order_address);
            deliveryBoy = itemView.findViewById(R.id.delivery_boy_tv);
            declinedInfo = itemView.findViewById(R.id.declined_info_tv);
            updateStatusBtn = itemView.findViewById(R.id.update_status_btn);
            assignDeliveryBtn = itemView.findViewById(R.id.assign_delivery_btn);
            callBtn = itemView.findViewById(R.id.quick_call_btn);
        }

        public void setData(QuickOrderModel model) {
            orderId.setText("ID: #" + model.getOrderId());
            status.setText(model.getOrderStatus());
            serviceName.setText(model.getServiceName());
            userName.setText(model.getUserName());
            userPhone.setText(model.getUserMobile());
            price.setText("₹" + model.getPrice());
            address.setText(model.getUserAddress());

            if (model.getDeliveryBoyName() != null && !model.getDeliveryBoyName().isEmpty()) {
                deliveryBoy.setText("Electrician: " + model.getDeliveryBoyName());
                deliveryBoy.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                declinedInfo.setVisibility(View.GONE);
            } else {
                deliveryBoy.setText("Electrician: Not Assigned");
                deliveryBoy.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                
                if (model.getLastDeclinedByBoyName() != null) {
                    declinedInfo.setVisibility(View.VISIBLE);
                    declinedInfo.setText("Declined by: " + model.getLastDeclinedByBoyName() + 
                            " (" + model.getDeclineReason() + ")");
                } else {
                    declinedInfo.setVisibility(View.GONE);
                }
            }

            if (model.getDateTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                date.setText(sdf.format(model.getDateTime().toDate()));
            } else {
                date.setText("N/A");
            }
        }
    }
}
