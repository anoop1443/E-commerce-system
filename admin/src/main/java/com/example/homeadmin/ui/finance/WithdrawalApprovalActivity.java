package com.example.homeadmin.ui.finance;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WithdrawalApprovalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noRequestsTv;
    private FirebaseFirestore db;
    private List<WithdrawalModel> requestList;
    private WithdrawalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_approval);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.withdrawal_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noRequestsTv = findViewById(R.id.no_requests_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();
        adapter = new WithdrawalAdapter(requestList);
        recyclerView.setAdapter(adapter);

        loadRequests();
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("withdrawal_requests")
                .whereEqualTo("status", "pending")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    requestList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        WithdrawalModel model = doc.toObject(WithdrawalModel.class);
                        if (model != null) {
                            model.setDocumentId(doc.getId());
                            requestList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    noRequestsTv.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void approveWithdrawal(WithdrawalModel model, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Approval")
                .setMessage("Are you sure you want to approve this withdrawal of ₹" + model.getAmount() + "?\nThis will deduct the amount from the delivery boy's balance.")
                .setPositiveButton("Approve", (dialog, which) -> {
                    processApproval(model, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processApproval(WithdrawalModel model, int position) {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference boyRef = db.collection("delivery_boy").document(model.getDeliveryBoyId());
        DocumentReference requestRef = db.collection("withdrawal_requests").document(model.getDocumentId());

        db.runTransaction(transaction -> {
            DocumentSnapshot boySnapshot = transaction.get(boyRef);
            Double currentBalance = boySnapshot.getDouble("main balance");
            if (currentBalance == null) currentBalance = 0.0;

            if (currentBalance < model.getAmount()) {
                throw new RuntimeException("Insufficient balance in boy's account.");
            }

            transaction.update(boyRef, "main balance", currentBalance - model.getAmount());
            transaction.update(requestRef, "status", "approved");
            transaction.update(requestRef, "approvedDate", com.google.firebase.firestore.FieldValue.serverTimestamp());
            return null;
        }).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Withdrawal Approved!", Toast.LENGTH_SHORT).show();
            requestList.remove(position);
            adapter.notifyItemRemoved(position);
            noRequestsTv.setVisibility(requestList.isEmpty() ? View.VISIBLE : View.GONE);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void rejectWithdrawal(WithdrawalModel model, int position) {
        db.collection("withdrawal_requests").document(model.getDocumentId())
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    requestList.remove(position);
                    adapter.notifyItemRemoved(position);
                });
    }

    private class WithdrawalAdapter extends RecyclerView.Adapter<WithdrawalAdapter.ViewHolder> {
        private List<WithdrawalModel> list;

        public WithdrawalAdapter(List<WithdrawalModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdrawal_approval, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WithdrawalModel model = list.get(position);
            holder.amount.setText("₹" + model.getAmount());
            holder.boyId.setText("ID: " + model.getDeliveryBoyId());
            
            if (model.getTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                holder.time.setText(sdf.format(model.getTimestamp().toDate()));
            }

            // Fetch boy name
            db.collection("delivery_boy").document(model.getDeliveryBoyId()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            holder.name.setText(doc.getString("name"));
                        }
                    });

            holder.approveBtn.setOnClickListener(v -> approveWithdrawal(model, position));
            holder.rejectBtn.setOnClickListener(v -> rejectWithdrawal(model, position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, amount, boyId, time;
            Button approveBtn, rejectBtn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.boy_name_text);
                amount = itemView.findViewById(R.id.amount_text);
                boyId = itemView.findViewById(R.id.boy_id_text);
                time = itemView.findViewById(R.id.timestamp_text);
                approveBtn = itemView.findViewById(R.id.approve_btn);
                rejectBtn = itemView.findViewById(R.id.reject_btn);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
