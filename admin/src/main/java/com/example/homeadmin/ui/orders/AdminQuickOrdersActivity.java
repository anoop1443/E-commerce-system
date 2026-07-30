package com.example.homeadmin.ui.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminQuickOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private AdminQuickOrderAdapter adapter;
    private List<QuickOrderModel> orderList;
    private TextView noOrdersText;
    private String orderStatus;
    private FirebaseFirestore db;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quick_orders);

        orderStatus = getIntent().getStringExtra("orderStatus");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (orderStatus != null) {
                getSupportActionBar().setTitle(orderStatus + " Quick Orders");
            }
        }

        db = FirebaseFirestore.getInstance();
        ordersRecyclerView = findViewById(R.id.quick_orders_recycler_view);
        noOrdersText = findViewById(R.id.no_orders_text);

        // Loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        orderList = new ArrayList<>();
        adapter = new AdminQuickOrderAdapter(orderList);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        if (orderStatus == null) {
            Log.e("AdminQuickOrders", "orderStatus is NULL");
            return;
        }

        Log.d("AdminQuickOrders", "Loading orders for status: " + orderStatus);

        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        Query query = db.collection("ORDERS_QUICK");

        if (orderStatus.equals("Ordered")) {
            query = query.whereEqualTo("orderStatus", "Ordered");
        } else if (orderStatus.equals("Assigned")) {
            // Assigned orders are those that have a deliveryBoyID and are not completed
            query = query.whereNotEqualTo("deliveryBoyID", null);
        } else if (orderStatus.equals("Shipped")) {
            query = query.whereIn("orderStatus", java.util.Arrays.asList("Shipped", "In Progress"));
        } else if (orderStatus.equals("Completed")) {
            query = query.whereIn("orderStatus", java.util.Arrays.asList("Delivered", "Completed"));
        } else if (orderStatus.equals("Cancelled")) {
            query = query.whereEqualTo("orderStatus", "Cancelled");
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (loadingDialog != null) loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        orderList.clear();
                        Log.d("AdminQuickOrders", "Query successful. Documents found: " + task.getResult().size());
                        for (DocumentSnapshot doc : task.getResult()) {
                            Log.d("AdminQuickOrders", "Document ID: " + doc.getId() + " data: " + doc.getData());
                            QuickOrderModel model = doc.toObject(QuickOrderModel.class);
                            if (model != null) {
                                model.setOrderId(doc.getId());
                                orderList.add(model);
                            } else {
                                Log.e("AdminQuickOrders", "Failed to convert document to QuickOrderModel: " + doc.getId());
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (orderList.isEmpty()) {
                            Log.d("AdminQuickOrders", "Order list is empty after processing");
                            ordersRecyclerView.setVisibility(View.GONE);
                            noOrdersText.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("AdminQuickOrders", "Total orders added to list: " + orderList.size());
                            ordersRecyclerView.setVisibility(View.VISIBLE);
                            noOrdersText.setVisibility(View.GONE);
                            Toast.makeText(this, "Orders Loaded", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("AdminQuickOrders", "Error loading orders from Firestore", task.getException());
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
