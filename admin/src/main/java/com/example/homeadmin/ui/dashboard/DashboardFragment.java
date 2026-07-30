package com.example.homeadmin.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.orders.AdminOrdersActivity;
import com.example.homeadmin.ui.orders.AdminQuickOrdersActivity;
import com.example.homeadmin.ui.orders.OrderAdapter;
import com.example.homeadmin.ui.orders.OrderModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView totalOrdersText, orderedCountText, packedCountText, shippedCountText, deliveredCountText, cancelledCountText, refundRequestsCountText;
    private TextView quickOrderedCountText, quickAssignedCountText, quickShippedCountText, quickCompletedCountText, quickCancelledCountText;
    private RecyclerView recentOrdersRecyclerview;
    private OrderAdapter adapter;
    private List<OrderModel> recentOrdersList = new ArrayList<>();
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        db = FirebaseFirestore.getInstance();

        totalOrdersText = root.findViewById(R.id.total_orders_count);
        orderedCountText = root.findViewById(R.id.ordered_count);
        packedCountText = root.findViewById(R.id.packed_count);
        shippedCountText = root.findViewById(R.id.shipped_count);
        deliveredCountText = root.findViewById(R.id.delivered_count);
        cancelledCountText = root.findViewById(R.id.cancelled_count);
        refundRequestsCountText = root.findViewById(R.id.refund_requests_count);

        quickOrderedCountText = root.findViewById(R.id.quick_ordered_count);
        quickAssignedCountText = root.findViewById(R.id.quick_assigned_count);
        quickShippedCountText = root.findViewById(R.id.quick_shipped_count);
        quickCompletedCountText = root.findViewById(R.id.quick_completed_count);
        quickCancelledCountText = root.findViewById(R.id.quick_cancelled_count);

        recentOrdersRecyclerview = root.findViewById(R.id.recent_orders_recyclerview);
        recentOrdersRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new OrderAdapter(recentOrdersList, getContext());
        recentOrdersRecyclerview.setAdapter(adapter);

        loadStatistics();
        loadQuickStatistics();
        loadRecentOrders();

        setupClickListeners(root);

        return root;
    }

    private void setupClickListeners(View root) {
        // Map Cards to their corresponding Statuses
        // : "Ordered", "Processing", "Out for Service", "Completed", "Cancelled"

        View orderedCard = (View) root.findViewById(R.id.ordered_count).getParent().getParent();
        orderedCard.setOnClickListener(v -> navigateToOrders("Ordered"));

        View packedCard = (View) root.findViewById(R.id.packed_count).getParent().getParent();
        packedCard.setOnClickListener(v -> navigateToOrders("Processing"));

        View shippedCard = (View) root.findViewById(R.id.shipped_count).getParent().getParent();
        shippedCard.setOnClickListener(v -> navigateToOrders("Out for Service"));

        View deliveredCard = (View) root.findViewById(R.id.delivered_count).getParent().getParent();
        deliveredCard.setOnClickListener(v -> navigateToOrders("Completed"));

        View cancelledCard = (View) root.findViewById(R.id.cancelled_count).getParent().getParent();
        cancelledCard.setOnClickListener(v -> navigateToOrders("Cancelled"));

        root.findViewById(R.id.refund_requests_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), com.example.homeadmin.ui.finance.RefundApprovalActivity.class));
        });

        View totalOrdersCard = (View) root.findViewById(R.id.total_orders_count).getParent().getParent();
        totalOrdersCard.setOnClickListener(v -> navigateToOrders("Total"));

        root.findViewById(R.id.quick_ordered_card).setOnClickListener(v -> navigateToQuickOrders("Ordered"));
        root.findViewById(R.id.quick_assigned_card).setOnClickListener(v -> navigateToQuickOrders("Assigned"));
        root.findViewById(R.id.quick_shipped_card).setOnClickListener(v -> navigateToQuickOrders("Shipped"));
        root.findViewById(R.id.quick_completed_card).setOnClickListener(v -> navigateToQuickOrders("Completed"));
        root.findViewById(R.id.quick_cancelled_card).setOnClickListener(v -> navigateToQuickOrders("Cancelled"));
    }

    private void navigateToQuickOrders(String status) {
        // This activity will be created next
        Intent intent = new Intent(getContext(), AdminQuickOrdersActivity.class);
        intent.putExtra("orderStatus", status);
        startActivity(intent);
    }

    private void navigateToOrders(String status) {
        Intent intent = new Intent(getContext(), AdminOrdersActivity.class);
        intent.putExtra("orderStatus", status);
        if (status.equals("Total")) {
            intent.putExtra("showFilter", true);
        }
        startActivity(intent);
    }

    private void loadStatistics() {
        // Querying the main ORDERS collection
        db.collection("ORDERS").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int total = 0, ordered = 0, packed = 0, shipped = 0, delivered = 0, cancelled = 0;
                for (DocumentSnapshot doc : task.getResult()) {
                    total++;
                    // Using "globalStatus" as defined in your structure
                    String status = doc.getString("globalStatus");
                    if (status != null) {
                        switch (status.toLowerCase()) {
                            case "ordered": ordered++; break;
                            case "processing":
                            case "packed": packed++; break;
                            case "out for service":
                            case "shipped": shipped++; break;
                            case "completed":
                            case "delivered": delivered++; break;
                            case "cancelled": cancelled++; break;
                        }
                    }
                }
                totalOrdersText.setText(String.valueOf(total));
                orderedCountText.setText(String.valueOf(ordered));
                packedCountText.setText(String.valueOf(packed));
                shippedCountText.setText(String.valueOf(shipped));
                deliveredCountText.setText(String.valueOf(delivered));
                cancelledCountText.setText(String.valueOf(cancelled));
            } else {
                Toast.makeText(getContext(), "Error loading stats", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Refund Statistics (using Collection Group for efficiency)
        db.collectionGroup("orderItems")
                .whereEqualTo("orderStatus", "Cancelled")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int refundPendingCount = 0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String refundStatus = doc.getString("refundStatus");
                        if (!"Refunded".equals(refundStatus)) {
                            refundPendingCount++;
                        }
                    }
                    if (refundRequestsCountText != null) {
                        refundRequestsCountText.setText(String.valueOf(refundPendingCount));
                    }
                })
                .addOnFailureListener(e -> Log.e("DashboardFragment", "Error loading refund stats", e));
    }

    private void loadQuickStatistics() {
        db.collection("ORDERS_QUICK").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int ordered = 0, assigned = 0, shipped = 0, completed = 0, cancelled = 0;
                for (DocumentSnapshot doc : task.getResult()) {
                    String status = doc.getString("orderStatus");
                    String assignedBoy = doc.getString("deliveryBoyID");

                    if (status != null) {
                        if (status.equalsIgnoreCase("Ordered")) ordered++;
                        else if (status.equalsIgnoreCase("Out for Service") || status.equalsIgnoreCase("Shipped") || status.equalsIgnoreCase("In Progress")) shipped++;
                        else if (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("Delivered")) completed++;
                        else if (status.equalsIgnoreCase("Cancelled")) cancelled++;

                        if (assignedBoy != null && !assignedBoy.isEmpty() && !status.equalsIgnoreCase("Completed") && !status.equalsIgnoreCase("Delivered") && !status.equalsIgnoreCase("Cancelled")) {
                            assigned++;
                        }
                    }
                }
                quickOrderedCountText.setText(String.valueOf(ordered));
                quickAssignedCountText.setText(String.valueOf(assigned));
                quickShippedCountText.setText(String.valueOf(shipped));
                quickCompletedCountText.setText(String.valueOf(completed));
                quickCancelledCountText.setText(String.valueOf(cancelled));
            }
        });
    }

    private void loadRecentOrders() {
        // Fetching latest 10 orders from ORDERS collection
        db.collection("ORDERS")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recentOrdersList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                             OrderModel orderModel = doc.toObject(OrderModel.class);
                             if (orderModel != null) {
                                 orderModel.setOrderID(doc.getId());
                                 recentOrdersList.add(orderModel);
                             }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
