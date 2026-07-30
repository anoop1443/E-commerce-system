package com.example.deliveryboy.orderfech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryboy.R;
import com.example.deliveryboy.order.OrderDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private static final String ARG_STATUS = "order_status";
    private String orderStatus;
    private RecyclerView orderListRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration normalItemListener, quickOrderListener;

    private List<Order> normalOrders = new ArrayList<>();
    private List<Order> quickOrders = new ArrayList<>();

    public OrderListFragment() {}

    public static OrderListFragment newInstance(String orderStatus) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, orderStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderStatus = getArguments().getString(ARG_STATUS);
        }
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        orderListRecyclerView = view.findViewById(R.id.orderListRecyclerView);
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderListRecyclerView.setAdapter(orderAdapter);

        fetchAssignedItems();
        fetchQuickOrders();

        return view;
    }

    private void fetchAssignedItems() {
        if (normalItemListener != null) normalItemListener.remove();
        
        String currentUserId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;
        if (currentUserId == null) return;

        Query query;
        if ("Ordered".equals(orderStatus)) {
            // Show all NEW tasks that are not yet assigned
            query = db.collectionGroup("orderItems")
                    .whereEqualTo("orderStatus", "Ordered")
                    .whereEqualTo("deliveryBoyID", null);
        } else {
            // Show tasks specifically assigned to THIS technician
            query = db.collectionGroup("orderItems")
                    .whereEqualTo("deliveryBoyID", currentUserId)
                    .whereEqualTo("orderStatus", orderStatus);
        }

        normalItemListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("OrderListFragment", "Listen failed.", e);
                return;
            }

            normalOrders.clear();
            if (snapshots != null) {
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
                    
                    normalOrders.add(order);
                }
            }
            refreshAdapter();
        });
    }

    private void fetchQuickOrders() {
        if (quickOrderListener != null) quickOrderListener.remove();

        String currentUserId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;
        if (currentUserId == null) return;

        // Map UI Status to Quick Order Status
        String quickStatus = orderStatus;
        if (orderStatus.equals("Out for Service")) quickStatus = "Assigned";
        else if (orderStatus.equals("Completed")) quickStatus = "Completed";

        Query query = db.collection("ORDERS_QUICK")
                .whereEqualTo("deliveryBoyID", currentUserId)
                .whereEqualTo("orderStatus", quickStatus);

        quickOrderListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w("OrderListFragment", "Quick Listen failed.", e);
                return;
            }

            quickOrders.clear();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    Order order = new Order();
                    order.setOrderID(doc.getId());
                    order.setStatus(doc.getString("orderStatus"));
                    order.setDeliveryBoyID(currentUserId);
                    order.setProductTitle(doc.getString("serviceName") + " (Quick Order)");
                    order.setQuickOrder(true);
                    order.setCustomerAddress(doc.getString("userAddress"));
                    order.setFullName(doc.getString("userName"));
                    order.setMobile(doc.getString("userMobile"));
                    
                    quickOrders.add(order);
                }
            }
            refreshAdapter();
        });
    }

    private void refreshAdapter() {
        orderList.clear();
        orderList.addAll(normalOrders);
        orderList.addAll(quickOrders);
        orderAdapter.notifyDataSetChanged();
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
    public void onDestroy() {
        super.onDestroy();
        if (normalItemListener != null) normalItemListener.remove();
        if (quickOrderListener != null) quickOrderListener.remove();
    }
}
