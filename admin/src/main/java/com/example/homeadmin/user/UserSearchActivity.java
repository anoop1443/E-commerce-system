package com.example.homeadmin.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserSearchActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private Button searchButton;
    private ProgressBar progressBar;
    private LinearLayout userDetailsContainer;
    private TextView userNameTextView, userAddressTextView;
    private RecyclerView ordersRecyclerView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        searchButton = findViewById(R.id.search_button);
        progressBar = findViewById(R.id.progress_bar);
        userDetailsContainer = findViewById(R.id.user_details_container);
        userNameTextView = findViewById(R.id.user_name_text_view);
        userAddressTextView = findViewById(R.id.user_address_text_view);
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(v -> searchUser());
    }

    private void searchUser() {
        String phoneNumber = "+91"+phoneNumberEditText.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        userDetailsContainer.setVisibility(View.GONE);

        db.collection("USER")
                .whereEqualTo("mobile", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            displayUserData(document);
                            break; // Assuming one user per phone number
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayUserData(QueryDocumentSnapshot userDocument) {
        userDetailsContainer.setVisibility(View.VISIBLE);

        // Display user's name
        userNameTextView.setText("Name: " + userDocument.getString("Full Name") + "\nEmail: " + userDocument.getString("email"));
        
        // Make container clickable to open full profile
        userDetailsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(UserSearchActivity.this, UserProfileActivity.class);
            intent.putExtra("uid", userDocument.getId());
            startActivity(intent);
        });

        // Fetch and display address from sub-collection
        db.collection("USER").document(userDocument.getId()).collection("MY_ADDRESSES")
                .get()
                .addOnSuccessListener(addressSnapshots -> {
                    if (!addressSnapshots.isEmpty()) {
                        StringBuilder addresses = new StringBuilder();
                        for (DocumentSnapshot addressDoc : addressSnapshots) {
                            // Look for the selected address
                            if (addressDoc.getBoolean("selected") != null && addressDoc.getBoolean("selected")) {
                                addresses.append(formatAddress(addressDoc));
                                break; // Show only the selected address
                            }
                        }
                        // If no selected address, show the first one
                        if (addresses.length() == 0) {
                            addresses.append(formatAddress(addressSnapshots.getDocuments().get(0)));
                        }
                        userAddressTextView.setText(addresses.toString());
                    } else {
                        userAddressTextView.setText("No address found.");
                    }
                });


        // Fetch and display orders from sub-collection
        db.collection("USER").document(userDocument.getId()).collection("USER_ORDERS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> orderIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        orderIds.add(doc.getString("orderID"));
                    }
                    fetchOrderDetails(orderIds);
                });
    }

    private void fetchOrderDetails(List<String> orderIds) {
        if (orderIds.isEmpty()) {
            // Handle case with no orders
            return;
        }

        db.collection("ORDERS").whereIn(FieldPath.documentId(), orderIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot orderDocument : queryDocumentSnapshots) {
                        Map<String, Object> orderData = orderDocument.getData();
                        orderData.put("documentId", orderDocument.getId()); // Add document ID to the map
                        orders.add(orderData);
                    }
                    ordersRecyclerView.setAdapter(new OrdersAdapter(orders));
                });
    }

    private String formatAddress(DocumentSnapshot doc) {
        return doc.getString("fullname") + "\n" +
                doc.getString("house") + ", " + doc.getString("area") + "\n" +
                doc.getString("city") + ", " + doc.getString("state") + " - " + doc.getString("pincode") + "\n" +
                "Mobile: " + doc.getString("mobile");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
