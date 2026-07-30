package com.example.homeadmin.ui.orders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homeadmin.R;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailsActivity";
    private String orderId;

    private RecyclerView orderItemsRecyclerView;
    private OrderDetailAdapter adapter;
    private List<MyOrderItemModel> orderItemList;

    private TextView customerNameTv, addressTv, mobileTv,totalAmount;
    private android.view.View callLayout;
    private String mobileNumber;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button ke liye

        // Intent se Order ID get karo
        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Order ID not found!", Toast.LENGTH_SHORT).show();
            finish(); // Activity band kar do agar ID nahi hai
            return;
        }

        db = FirebaseFirestore.getInstance();

        // Views ko initialize karo
        customerNameTv = findViewById(R.id.detail_customerNameTv_tv);
        addressTv = findViewById(R.id.detail_address_tv);
        mobileTv = findViewById(R.id.detail_mobile_tv);
        totalAmount = findViewById(R.id.detail_Total_amount_tv);
        callLayout = findViewById(R.id.call_customer_layout);

        callLayout.setOnClickListener(v -> {
            if (mobileNumber != null && !mobileNumber.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobileNumber));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Mobile number not available", Toast.LENGTH_SHORT).show();
            }
        });

        orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderItemList = new ArrayList<>();
        adapter = new OrderDetailAdapter(orderItemList, this);
        orderItemsRecyclerView.setAdapter(adapter);

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        // Step 1: Main order document se address/customer details fetch karo
        db.collection("ORDERS").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String address = documentSnapshot.getString("address");
                        String pincode = documentSnapshot.getString("pinCode");
                        mobileNumber = documentSnapshot.getString("mobile");
                        String amount = String.valueOf(documentSnapshot.get("totalAmount"));

                        customerNameTv.setText(name);
                        addressTv.setText(address + ", " + pincode);
                        mobileTv.setText(mobileNumber);
                        totalAmount.setText("₹" + amount);

                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Error fetching main order details", e));

        // Step 2: 'orderItems' sub-collection se saare products fetch karo
        db.collection("ORDERS").document(orderId).collection("orderItems")
                .orderBy("orderStatus", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        Toast.makeText(this, "Failed to load items.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value == null) {
                        return;
                    }

                    orderItemList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        MyOrderItemModel model = doc.toObject(MyOrderItemModel.class);
                        if (model != null) {
                            // Fields mapping for sub-collection items
                            model.setOrderId(orderId);
                            model.setProductId(doc.getId());
                            model.setOrderStatus(doc.getString("orderStatus"));
                            model.setProductTitle(doc.getString("productTitle"));
                            model.setProductImage(doc.getString("productImage"));
                            
                            Object qtyObj = doc.get("productQuantity");
                            long quantity = (qtyObj instanceof Number) ? ((Number) qtyObj).longValue() : 0;
                            model.setQuantity(quantity);
                            
                            model.setProductPrice(doc.getString("productPrice"));
                            model.setDeliveredDate(doc.getDate("deliveredDate"));
                            orderItemList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Back button ka kaam karega
        return true;
    }
}


