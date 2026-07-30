package com.example.deliveryboy.order;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Glide library ka istemal karein
import com.example.deliveryboy.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<MyOrderItemModel> itemList;
    private Context context;
    private SimpleDateFormat sdf;
    private FirebaseFirestore db;
    private String deliveryBoyName = "Unknown"; // Default

    public OrderDetailAdapter(List<MyOrderItemModel> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        this.sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        this.db = FirebaseFirestore.getInstance();
        fetchDeliveryBoyName();
    }

    private void fetchDeliveryBoyName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("delivery_boy").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            deliveryBoyName = documentSnapshot.getString("name");
                        }
                    });
        }
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
        
        // Handle Status Styling
        if ("Completed".equals(item.getOrderStatus()) || "Delivered".equals(item.getOrderStatus())) {
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.updateStatusBtn.setVisibility(View.GONE); // Hide if already completed
        } else if ("Cancelled".equals(item.getOrderStatus())) {
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.updateStatusBtn.setVisibility(View.GONE); // Hide if already cancelled
        } else {
            holder.status.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.updateStatusBtn.setVisibility(View.VISIBLE);
        }

        String orderId = item.getOrderID();
        String productId = item.getProductID();

        // Image load karein
        Glide.with(context).load(item.getProductImage()).placeholder(R.drawable.outline_contacts_product_24).into(holder.image);

        holder.updateStatusBtn.setOnClickListener(v -> {
            // Check if it's a Quick Order virtual item or Normal Order item
            if (item.getProductTitle().contains("(Quick Service)")) {
                showQuickStatusUpdateDialog(orderId);
            } else {
                showStatusUpdateDialog(orderId, productId);
            }
        });
    }

    private void showQuickStatusUpdateDialog(String orderId) {
        String[] statusOptions = {"Completed", "Cancelled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Complete Service Task");
        builder.setItems(statusOptions, (dialog, which) -> {

            String selectedStatus = statusOptions[which];
            Map<String, Object> updates = new HashMap<>();
            updates.put("orderStatus", selectedStatus);

            if ("Completed".equals(selectedStatus)) {
                updates.put("completedDate", FieldValue.serverTimestamp());
                if (context instanceof OrderDetailsActivity) {
                    ((OrderDetailsActivity) context).handleItemDelivery(orderId, null, updates, true);
                } else {
                    updateQuickOrderStatus(orderId, selectedStatus);
                }
            } else if ("Cancelled".equals(selectedStatus)) {

                updateQuickOrderStatus(orderId, selectedStatus);
            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void showStatusUpdateDialog(String orderId, String productId) {
        String[] statusOptions = {"Completed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Item Status");
        builder.setItems(statusOptions, (dialog, which) -> {
            String selectedStatus = statusOptions[which];

            if ("Completed".equals(selectedStatus)) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("orderStatus", "Completed");
                updates.put("deliveredDate", FieldValue.serverTimestamp());
                
                // Ask activity to handle delivery with OTP
                if (context instanceof OrderDetailsActivity) {
                    ((OrderDetailsActivity) context).handleItemDelivery(orderId, productId, updates, false);
                } else {
                    updateFirebaseOrder(orderId, productId, updates);
                }
            } else if ("Cancelled".equals(selectedStatus)) {
                showCancellationReasonDialog(orderId, productId);
            }
        });
        builder.show();
    }

    private void showCancellationReasonDialog(String orderId, String productId) {
        String[] reasons = {
            "Customer not available",
            "Customer refused to accept",
            "Wrong address provided",
            "Customer requested cancellation",
            "Payment issues",
            "Other"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Reason for Cancellation");
        builder.setItems(reasons, (dialog, which) -> {
            String selectedReason = reasons[which];
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("orderStatus", "Cancelled");
            updates.put("cancelledDate", FieldValue.serverTimestamp());
            updates.put("cancelledBy", "Delivery Boy");
            updates.put("boyName", deliveryBoyName);
            updates.put("cancellationReasonByBoy", selectedReason); // Specific field for boy's reason

            updateFirebaseOrder(orderId, productId, updates);
        });
        builder.show();
    }

    public void updateFirebaseOrder(String orderId, String productID, Map<String, Object> updates) {
        // Capture Location before update
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(context).getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            updates.put("deliveryBoyLocation", new GeoPoint(location.getLatitude(), location.getLongitude()));
                        }
                        performFirebaseUpdate(orderId, productID, updates);
                    })
                    .addOnFailureListener(e -> performFirebaseUpdate(orderId, productID, updates));
        } else {
            performFirebaseUpdate(orderId, productID, updates);
        }
    }

    private void performFirebaseUpdate(String orderId, String productID, Map<String, Object> updates) {
        // Automatically add timestamp based on status
        String status = (String) updates.get("orderStatus");
        if (status != null) {
            switch (status) {
                case "Processing": updates.put("packedDate", FieldValue.serverTimestamp()); break;
                case "Out for Service": updates.put("shippedDate", FieldValue.serverTimestamp()); break;
                case "Completed": updates.put("deliveredDate", FieldValue.serverTimestamp()); break;
                case "Cancelled": updates.put("cancelledDate", FieldValue.serverTimestamp()); break;
            }
        }

        db.collection("ORDERS").document(orderId).collection("orderItems").document(productID)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Status Updated with Time!", Toast.LENGTH_SHORT).show();
                    
                    finalizeItemEarning(orderId, status, false);
                    checkAndUpdateGlobalStatus(orderId);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show());
    }

    private void finalizeItemEarning(String orderId, String status, boolean isQuickOrder) {
        double earningAmount = ("Completed".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status)) ? 50.0 : 20.0;
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DocumentReference boyRef = db.collection("delivery_boy").document(uid);
        
        Map<String, Object> earningData = new HashMap<>();
        earningData.put("orderId", orderId);
        earningData.put("amount", earningAmount);
        earningData.put("status", status);
        earningData.put("timestamp", FieldValue.serverTimestamp());
        earningData.put("isQuickOrder", isQuickOrder);

        db.runTransaction(transaction -> {
            transaction.update(boyRef, "main balance", FieldValue.increment(earningAmount));
            DocumentReference historyRef = boyRef.collection("earnings_history").document();
            transaction.set(historyRef, earningData);
            return null;
        }).addOnFailureListener(e -> Toast.makeText(context, "Earning Update Failed", Toast.LENGTH_SHORT).show());
    }

    private void checkAndUpdateGlobalStatus(String orderId) {
        db.collection("ORDERS").document(orderId).collection("orderItems").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean allDone = true;
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        String s = doc.getString("orderStatus");
                        if (!"Completed".equalsIgnoreCase(s) && !"Delivered".equalsIgnoreCase(s) && !"Cancelled".equalsIgnoreCase(s)) {
                            allDone = false;
                            break;
                        }
                    }
                    if (allDone) {
                        db.collection("ORDERS").document(orderId).update("globalStatus", "Completed");
                    }
                });
    }

    public void updateQuickOrderStatus(String orderId, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("orderStatus", status);
        if ("Completed".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status)) {
            updates.put("completedDate", FieldValue.serverTimestamp());
        } else if ("Cancelled".equalsIgnoreCase(status)) {
            quickShowCancellationReasonDialog(orderId);
        }

        // Capture Location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(context).getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            updates.put("deliveryBoyLocation", new GeoPoint(location.getLatitude(), location.getLongitude()));
                        }
                        performQuickOrderUpdate(orderId, updates);
                    })
                    .addOnFailureListener(e -> performQuickOrderUpdate(orderId, updates));
        } else {
            performQuickOrderUpdate(orderId, updates);
        }
    }


    private void quickShowCancellationReasonDialog(String orderId) {
        String[] reasons = {
                "Customer not available",
                "Customer refused to accept",
                "Wrong address provided",
                "Customer requested cancellation",
                "Payment issues",
                "Other"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Reason for Cancellation");
        builder.setItems(reasons, (dialog, which) -> {
            String selectedReason = reasons[which];

            Map<String, Object> updates = new HashMap<>();
            updates.put("orderStatus", "Cancelled");
            updates.put("cancelledDate", FieldValue.serverTimestamp());
            updates.put("cancelledBy", "Delivery Boy");
            updates.put("boyName", deliveryBoyName);
            updates.put("cancellationReasonByBoy", selectedReason); // Specific field for boy's reason

            performQuickOrderUpdate(orderId,updates);
            //updateFirebaseOrder(orderId, productId, updates);
        });
        builder.show();
    }


    private void performQuickOrderUpdate(String orderId, Map<String, Object> updates) {
        String status = (String) updates.get("orderStatus");
        if (status != null) {
            if ("Completed".equalsIgnoreCase(status)) {
                updates.put("deliveredDate", FieldValue.serverTimestamp());
            } else if ("Out for Service".equalsIgnoreCase(status)) {
                updates.put("shippedDate", FieldValue.serverTimestamp());
            }
        }

        db.collection("ORDERS_QUICK").document(orderId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Service " + status, Toast.LENGTH_SHORT).show();
                    finalizeItemEarning(orderId, status, true);
                });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView  status, title, priceQuantity;
        Button updateStatusBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            status = itemView.findViewById(R.id.item_Status);
            title = itemView.findViewById(R.id.item_title);
            priceQuantity = itemView.findViewById(R.id.item_price_quantity);
            updateStatusBtn = itemView.findViewById(R.id.update_status_btn_orderDetail);
        }
    }
}

