package com.example.deliveryboy.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryboy.R;
import com.example.deliveryboy.order.OrderDetailsActivity;
import com.example.deliveryboy.orderfech.Order;
import com.example.deliveryboy.orderfech.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private List<Order> normalItems = new ArrayList<>();
    private List<Order> quickOrders = new ArrayList<>();
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration normalItemListener, quickOrdersListener;
    
    private ProgressBar progressBar;
    private TextView taskCountText, noTasksText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.unifiedOrderRecyclerView);
        progressBar = view.findViewById(R.id.homeProgressBar);
        taskCountText = view.findViewById(R.id.task_count_text);
        noTasksText = view.findViewById(R.id.noTasksText);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchActiveTasks();

        return view;
    }

    private void fetchActiveTasks() {
        String currentUserId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) return;

        progressBar.setVisibility(View.VISIBLE);

        // 1. Listen for Normal Items assigned to THIS technician (Active only)
        Query itemQuery = db.collectionGroup("orderItems")
                .whereEqualTo("deliveryBoyID", currentUserId)
                .whereIn("orderStatus", java.util.Arrays.asList("Ordered", "Processing", "Out for Service", "Out for Delivery"));

        normalItemListener = itemQuery.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("HomeFragment", "Items listen failed.", e);
                return;
            }
            updateCombinedList("NORMAL_ITEMS", snapshots);
        });

        // 2. Listen for Quick Orders assigned to THIS technician (Active only)
        Query quickQuery = db.collection("ORDERS_QUICK")
                .whereEqualTo("deliveryBoyID", currentUserId)
                .whereIn("orderStatus", java.util.Arrays.asList("Ordered", "Assigned", "Accepted"));

        quickOrdersListener = quickQuery.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("HomeFragment", "Quick Orders listen failed.", e);
                return;
            }
            updateCombinedList("QUICK_ORDERS", snapshots);
        });
    }

    private synchronized void updateCombinedList(String type, QuerySnapshot snapshots) {
        String currentUserId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
        if (snapshots == null || currentUserId == null) return;

        if (type.equals("NORMAL_ITEMS")) {
            normalItems.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Order order = new Order();
                order.setOrderID(doc.getString("orderID"));
                order.setProductID(doc.getId());
                order.setProductTitle(doc.getString("productTitle"));
                order.setStatus(doc.getString("orderStatus"));
                order.setDeliveryBoyID(currentUserId);
                order.setQuickOrder(false);
                order.setImageUrl(doc.getString("productImage"));
                order.setCustomerAddress(doc.getString("address"));
                order.setFullName(doc.getString("fullName"));
                order.setMobile(doc.getString("mobile"));
                
                normalItems.add(order);
            }
        } else {
            quickOrders.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Order order = new Order();
                order.setOrderID(doc.getId());
                order.setStatus(doc.getString("orderStatus"));
                order.setDeliveryBoyID(currentUserId);
                order.setProductTitle(doc.getString("serviceName") + " (Quick Service)");
                order.setQuickOrder(true);
                order.setCustomerAddress(doc.getString("userAddress"));
                order.setFullName(doc.getString("userName"));
                order.setMobile(doc.getString("userMobile"));
                
                quickOrders.add(order);
            }
        }
        refreshUI();
    }

    private void refreshUI() {
        if (!isAdded()) return;
        
        progressBar.setVisibility(View.GONE);
        orderList.clear();
        
        // Combine and Sort
        orderList.addAll(normalItems);
        orderList.addAll(quickOrders);
        
        // Sorting: Active tasks at top
        java.util.Collections.sort(orderList, (o1, o2) -> {
            boolean active1 = "Out for Service".equals(o1.getStatus()) || "Assigned".equals(o1.getStatus()) || "Accepted".equals(o1.getStatus());
            boolean active2 = "Out for Service".equals(o2.getStatus()) || "Assigned".equals(o2.getStatus()) || "Accepted".equals(o2.getStatus());
            if (active1 && !active2) return -1;
            if (!active1 && active2) return 1;
            return 0;
        });
        
        orderAdapter.notifyDataSetChanged();

        int total = orderList.size();
        taskCountText.setText(total + " Active Tasks Found");
        noTasksText.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
        intent.putExtra("ORDER_ID", order.getOrderID());
        intent.putExtra("PRODUCT_ID", order.getProductID());
        intent.putExtra("STATUS", order.getStatus());
        intent.putExtra("IS_QUICK_ORDER", order.isQuickOrder());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (normalItemListener != null) normalItemListener.remove();
        if (quickOrdersListener != null) quickOrdersListener.remove();
    }
}
