package com.example.homeadmin.ui.management;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ElectricianProfileActivity extends AppCompatActivity {

    private String electricianUid;
    private FirebaseFirestore db;
    private CircleImageView profileImage;
    private TextView nameTv, statusBadge, balanceTv, servicesTv, noEarningsTv;
    private EditText skillsInput, aadhaarInput, phoneInput;
    private Button saveBtn;
    private ProgressBar progressBar;
    private RecyclerView earningsRv;
    private EarningsAdapter earningsAdapter;
    private List<EarningRecord> earningsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrician_profile);

        electricianUid = getIntent().getStringExtra("uid");
        if (electricianUid == null) {
            Toast.makeText(this, "ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        initViews();
        setupRecyclerView();
        loadProfile();
        loadEarningsHistory();

        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileImage = findViewById(R.id.profile_image);
        nameTv = findViewById(R.id.electrician_name);
        statusBadge = findViewById(R.id.status_badge);
        balanceTv = findViewById(R.id.wallet_balance);
        servicesTv = findViewById(R.id.services_count);
        skillsInput = findViewById(R.id.edit_skills);
        aadhaarInput = findViewById(R.id.edit_aadhaar);
        phoneInput = findViewById(R.id.edit_phone);
        saveBtn = findViewById(R.id.save_profile_btn);
        progressBar = findViewById(R.id.profile_progress);
        earningsRv = findViewById(R.id.earnings_recycler_view);
        noEarningsTv = findViewById(R.id.no_earnings_text);
    }

    private void setupRecyclerView() {
        earningsList = new ArrayList<>();
        earningsAdapter = new EarningsAdapter(earningsList);
        earningsRv.setLayoutManager(new LinearLayoutManager(this));
        earningsRv.setAdapter(earningsAdapter);
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("delivery_boy").document(electricianUid).get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);
                    if (doc.exists()) {
                        nameTv.setText(doc.getString("name"));
                        phoneInput.setText(doc.getString("phone"));
                        aadhaarInput.setText(doc.getString("aadhaarNumber"));
                        skillsInput.setText(doc.getString("skills"));
                        
                        Double balance = doc.getDouble("main balance");
                        balanceTv.setText("₹" + String.format(Locale.getDefault(), "%.2f", balance != null ? balance : 0.0));
                        
                        Long services = doc.getLong("totalServicesCompleted");
                        servicesTv.setText((services != null ? services : 0) + " Services");

                        Boolean isOnline = doc.getBoolean("isOnline");
                        if (isOnline != null && isOnline) {
                            statusBadge.setText("Online");
                            statusBadge.setTextColor(Color.GREEN);
                        } else {
                            statusBadge.setText("Offline");
                            statusBadge.setTextColor(Color.GRAY);
                        }

                        Glide.with(this).load(doc.getString("profileImage"))
                                .placeholder(R.drawable.ic_person).into(profileImage);
                    }
                });
    }

    private void loadEarningsHistory() {
        db.collection("delivery_boy").document(electricianUid)
                .collection("earnings_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    earningsList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        EarningRecord record = new EarningRecord();
                        record.orderId = doc.getString("orderId");
                        record.amount = doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0;
                        record.timestamp = doc.getTimestamp("timestamp");
                        earningsList.add(record);
                    }
                    earningsAdapter.notifyDataSetChanged();
                    noEarningsTv.setVisibility(earningsList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void saveProfile() {
        String skills = skillsInput.getText().toString().trim();
        String aadhaar = aadhaarInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> update = new HashMap<>();
        update.put("skills", skills);
        update.put("aadhaarNumber", aadhaar);
        update.put("phone", phone);

        db.collection("delivery_boy").document(electricianUid)
                .update(update)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class EarningRecord {
        String orderId;
        double amount;
        Timestamp timestamp;
    }

    private class EarningsAdapter extends RecyclerView.Adapter<EarningsAdapter.ViewHolder> {
        private final List<EarningRecord> list;

        public EarningsAdapter(List<EarningRecord> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning_history, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EarningRecord record = list.get(position);
            holder.orderId.setText("Order #" + record.orderId);
            holder.amount.setText("+ ₹" + String.format(Locale.getDefault(), "%.2f", record.amount));
            
            if (record.timestamp != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                holder.date.setText(sdf.format(record.timestamp.toDate()));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView orderId, date, amount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                orderId = itemView.findViewById(R.id.order_id_text);
                date = itemView.findViewById(R.id.date_text);
                amount = itemView.findViewById(R.id.amount_text);
            }
        }
    }
}
