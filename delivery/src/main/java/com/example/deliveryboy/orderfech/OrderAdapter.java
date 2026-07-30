package com.example.deliveryboy.orderfech;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deliveryboy.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView orderTitle;
        private TextView orderStatus, orderStatusBadge;
        private ImageView orderImage, actionArrow;
        private Button acceptButton, declineButton;
        private FirebaseFirestore db;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTitle = itemView.findViewById(R.id.orderTitle);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderStatusBadge = itemView.findViewById(R.id.orderStatusBadge);
            orderImage = itemView.findViewById(R.id.orderImage);
            actionArrow = itemView.findViewById(R.id.action_arrow);
            acceptButton = itemView.findViewById(R.id.buttonAcceptOrder);
            declineButton = itemView.findViewById(R.id.buttonDeclineOrder);
            itemView.setOnClickListener(this);
            db = FirebaseFirestore.getInstance();
        }

        public void bind(Order order, int position) {
            String otp = generateSecureOtp();
            orderTitle.setText(order.getProductTitle() != null ? order.getProductTitle() : "N/A");
            
            String statusText = "Status: " + (order.getStatus() != null ? order.getStatus() : "N/A");
            if (order.getCustomerAddress() != null) {
                statusText += "\nLoc: " + order.getCustomerAddress();
            }
            orderStatus.setText(statusText);

            // Update Badge based on status
            if ("Ordered".equals(order.getStatus())) {
                orderStatusBadge.setText("NEW TASK");
                orderStatusBadge.getBackground().setTint(0xFFFFF3E0); // Light Orange
                orderStatusBadge.setTextColor(0xFFE65100); // Dark Orange
            } else if ("Out for Service".equals(order.getStatus()) || "Assigned".equals(order.getStatus()) || "Out for Delivery".equals(order.getStatus())) {
                orderStatusBadge.setText("ACTIVE");
                orderStatusBadge.getBackground().setTint(0xFFE8F5E9); // Light Green
                orderStatusBadge.setTextColor(0xFF2E7D32); // Dark Green
            } else if ("Completed".equals(order.getStatus()) || "Delivered".equals(order.getStatus())) {
                orderStatusBadge.setText("FINISHED");
                orderStatusBadge.getBackground().setTint(0xFFE3F2FD); // Light Blue
                orderStatusBadge.setTextColor(0xFF1565C0); // Dark Blue
            }

            if (order.getImageUrl() != null && !order.getImageUrl().isEmpty()) {
                Glide.with(context).load(order.getImageUrl()).into(orderImage);
            } else {
                orderImage.setImageResource(order.isQuickOrder() ? R.drawable.green_circle_button : android.R.drawable.ic_lock_idle_low_battery);
            }

            // Show accept button only for pending orders
            if ("Ordered".equals(order.getStatus())) {
                acceptButton.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.VISIBLE);
                acceptButton.setText("Accept Task");
                actionArrow.setVisibility(View.GONE);
                
                acceptButton.setOnClickListener(v -> {
                    String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                    if (uid == null) return;

                    // Fetch boy name first
                    db.collection("delivery_boy").document(uid).get().addOnSuccessListener(boyDoc -> {
                        String name = boyDoc.getString("name");
                        String finalName = (name != null) ? name : "Technician";

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("deliveryBoyID", uid);
                        updates.put("deliveryBoyName", finalName);

                        if (order.isQuickOrder()) {
                            updates.put("orderStatus", "Assigned");
                            updates.put("shippedDate", FieldValue.serverTimestamp());
                            updates.put("otp", Long.parseLong(otp)); // New: Save OTP for Quick Orders
                            db.collection("ORDERS_QUICK").document(order.getOrderID())
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Task Accepted!", Toast.LENGTH_SHORT).show());
                        } else {
                            updates.put("orderStatus", "Out for Service");
                            updates.put("shippedDate", FieldValue.serverTimestamp());
                            updates.put("otp", Long.parseLong(otp)); // Save OTP for Normal Items

                            // Update specific item
                            db.collection("ORDERS").document(order.getOrderID())
                                    .collection("orderItems").document(order.getProductID())
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Task Accepted!", Toast.LENGTH_SHORT).show();
                                        // Optional: Update main order status to 'Out for Service' if needed
                                        db.collection("ORDERS").document(order.getOrderID()).update("globalStatus", "Out for Service");
                                    });
                        }
                    });
                });

                declineButton.setOnClickListener(v -> {
                    showDeclineDialog(order);
                });
            } else {
                acceptButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.GONE);
                actionArrow.setVisibility(View.VISIBLE);
            }
        }

        private void showDeclineDialog(Order order) {
            String[] reasons = {"Too Busy", "Out of Station", "Health Issue", "Vehicle Problem", "Other"};
            
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Reason for Decline");
            builder.setItems(reasons, (dialog, which) -> {
                String selectedReason = reasons[which];
                if ("Other".equals(selectedReason)) {
                    showCustomReasonDialog(order);
                } else {
                    processDecline(order, selectedReason);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        private void showCustomReasonDialog(Order order) {
            EditText input = new EditText(context);
            input.setHint("Enter reason here...");
            
            new AlertDialog.Builder(context)
                    .setTitle("Custom Decline Reason")
                    .setView(input)
                    .setPositiveButton("Submit", (dialog, which) -> {
                        String reason = input.getText().toString().trim();
                        if (!reason.isEmpty()) {
                            processDecline(order, reason);
                        } else {
                            Toast.makeText(context, "Reason is required!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Back", (dialog, which) -> showDeclineDialog(order))
                    .show();
        }

        private void processDecline(Order order, String reason) {
            String collection = order.isQuickOrder() ? "ORDERS_QUICK" : "ORDERS";
            String currentBoyName = "Electrician"; // Default or fetch from SharedPreferences/Auth if available
            
            // Attempt to get name from Firestore if not passed
            db.collection("delivery_boy").document(com.google.firebase.auth.FirebaseAuth.getInstance().getUid())
                    .get().addOnSuccessListener(doc -> {
                        String boyName = doc.getString("name");
                        String finalName = (boyName != null) ? boyName : currentBoyName;

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("deliveryBoyID", null);
                        updates.put("deliveryBoyName", null);
                        updates.put("lastDeclinedByBoyID", com.google.firebase.auth.FirebaseAuth.getInstance().getUid());
                        updates.put("lastDeclinedByBoyName", finalName);
                        updates.put("declineReason", reason);

                        db.collection(collection).document(order.getOrderID())
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Task Declined & Returned to Admin", Toast.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to decline: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onOrderClick(orderList.get(position));
            }
        }
    }

    public static String generateSecureOtp() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }
}
