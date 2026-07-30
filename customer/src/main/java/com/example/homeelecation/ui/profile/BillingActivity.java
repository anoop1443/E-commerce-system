package com.example.homeelecation.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.orders.QuickOrderDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BillingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View emptyState;
    private List<DocumentSnapshot> billList = new ArrayList<>();
    private BillAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        Toolbar toolbar = findViewById(R.id.toolbar_billing);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.billing_recycler_view);
        emptyState = findViewById(R.id.empty_billing_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BillAdapter();
        recyclerView.setAdapter(adapter);

        loadBills();
    }

    private void loadBills() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // Fetching only Quick Orders that have a final bill for now
        db.collection("ORDERS_QUICK")
                .whereEqualTo("userId", uid)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        billList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            if (doc.contains("finalTotal")) {
                                billList.add(doc);
                            }
                        }
                        
                        if (billList.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            TextView title = emptyState.findViewById(R.id.empty_state_title);
                            TextView desc = emptyState.findViewById(R.id.empty_state_desc);
                            title.setText("No Bills Found");
                            desc.setText("Once your services are completed, your invoices will appear here.");
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DocumentSnapshot doc = billList.get(position);
            
            holder.orderId.setText("Order #" + doc.getString("orderId"));
            holder.serviceName.setText(doc.getString("serviceName"));
            holder.amount.setText("₹" + doc.getString("finalTotal"));
            
            if (doc.getDate("dateTime") != null) {
                holder.date.setText(dateFormat.format(doc.getDate("dateTime")));
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(BillingActivity.this, QuickOrderDetailsActivity.class);
                intent.putExtra("ORDER_ID", doc.getString("orderId"));
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return billList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView orderId, serviceName, amount, date;
            ViewHolder(View v) {
                super(v);
                orderId = v.findViewById(R.id.bill_order_id);
                serviceName = v.findViewById(R.id.bill_service_name);
                amount = v.findViewById(R.id.bill_amount);
                date = v.findViewById(R.id.bill_date);
            }
        }
    }
}
