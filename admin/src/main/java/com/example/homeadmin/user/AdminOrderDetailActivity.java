package com.example.homeadmin.user;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private RecyclerView orderItemsRecyclerView;
    private TextView shippingAddressTextView;
    private FirebaseFirestore db;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        shippingAddressTextView = findViewById(R.id.shipping_address_text_view);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderId = getIntent().getStringExtra("order_id");

        if (orderId != null) {
            loadOrderItems();
        }
    }

    private void loadOrderItems() {
        db.collection("ORDERS").document(orderId).collection("orderItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        orderItems.add(doc.toObject(OrderItem.class));
                        // Set shipping address from the first item
                        if (shippingAddressTextView.getText().toString().equals("Shipping Address:")) {
                             shippingAddressTextView.append("\n" + doc.getString("address"));
                        }
                    }
                    orderItemsRecyclerView.setAdapter(new AdminOrderItemsAdapter(orderItems));
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
