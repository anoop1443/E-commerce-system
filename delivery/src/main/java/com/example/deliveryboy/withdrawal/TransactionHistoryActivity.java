package com.example.deliveryboy.withdrawal;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliveryboy.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<TransactionRecord> transactionList;
    private ProgressBar progressBar;
    private TextView noTxnText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        Toolbar toolbar = findViewById(R.id.txn_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.transactionRecyclerView);
        progressBar = findViewById(R.id.txnProgressBar);
        noTxnText = findViewById(R.id.noTxnText);

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupSwipeActions();
        loadTransactions();
    }

    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TransactionRecord txn = transactionList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    showRaiseTicketSheet(txn);
                    adapter.notifyItemChanged(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    if ("Pending".equalsIgnoreCase(txn.getStatus())) {
                        showCancelConfirmation(txn, position);
                    } else {
                        Toast.makeText(TransactionHistoryActivity.this, "Only pending requests can be cancelled", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                android.graphics.Paint paint = new android.graphics.Paint();
                android.graphics.drawable.Drawable icon;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX > 0) { // Swipe Right (Cancel)
                        paint.setColor(android.graphics.Color.parseColor("#E53935")); // Red
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                        
                        icon = androidx.core.content.ContextCompat.getDrawable(TransactionHistoryActivity.this, android.R.drawable.ic_menu_close_clear_cancel);
                        if (icon != null) {
                            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                            int iconBottom = iconTop + icon.getIntrinsicHeight();
                            int iconLeft = itemView.getLeft() + iconMargin;
                            int iconRight = iconLeft + icon.getIntrinsicWidth();
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            icon.draw(c);
                        }
                    } else if (dX < 0) { // Swipe Left (Report)
                        paint.setColor(android.graphics.Color.parseColor("#1976D2")); // Blue
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                        
                        icon = androidx.core.content.ContextCompat.getDrawable(TransactionHistoryActivity.this, android.R.drawable.ic_menu_help);
                        if (icon != null) {
                            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                            int iconBottom = iconTop + icon.getIntrinsicHeight();
                            int iconRight = itemView.getRight() - iconMargin;
                            int iconLeft = iconRight - icon.getIntrinsicWidth();
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            icon.draw(c);
                        }
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }

    private void showRaiseTicketSheet(TransactionRecord txn) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_raise_ticket, null);
        sheet.setContentView(view);

        TextView txnIdText = view.findViewById(R.id.sheet_txn_id);
        AutoCompleteTextView reasonDropdown = view.findViewById(R.id.edit_ticket_reason);
        TextInputLayout customLayout = view.findViewById(R.id.custom_reason_layout);
        TextInputEditText customEdit = view.findViewById(R.id.edit_custom_reason);
        Button submitBtn = view.findViewById(R.id.btn_submit_ticket);

        txnIdText.setText("Transaction ID: #" + txn.getTransactionId());

        String[] reasons = {"Amount not received", "Wrong amount credited", "Delayed payment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reasons);
        reasonDropdown.setAdapter(adapter);

        reasonDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            customLayout.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
        });

        submitBtn.setOnClickListener(v -> {
            String reason = reasonDropdown.getText().toString();
            String message = customEdit.getText().toString().trim();

            if (reason.isEmpty()) {
                Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show();
                return;
            }

            submitSupportTicket(txn, reason, message, sheet);
        });

        sheet.show();
    }

    private void submitSupportTicket(TransactionRecord txn, String reason, String message, BottomSheetDialog sheet) {
        if (mAuth.getCurrentUser() == null) return;

        // Shorter Numeric Ticket ID (6 Digits)
        int randomId = (int) (Math.random() * 900000) + 100000;
        String ticketId = String.valueOf(randomId);

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("ticketId", ticketId);
        ticket.put("transactionId", txn.getTransactionId());
        ticket.put("deliveryBoyId", mAuth.getCurrentUser().getUid());
        ticket.put("reason", reason);
        ticket.put("message", message);
        ticket.put("status", "OPEN");
        ticket.put("createdDate", FieldValue.serverTimestamp());
        ticket.put("type", "WITHDRAWAL_ISSUE");
        
        // Detailed Timestamps
        ticket.put("resolvedDate", null);
        ticket.put("closedDate", null);
        ticket.put("adminNote", "");
        ticket.put("lastUpdateDate", FieldValue.serverTimestamp());

        db.collection("SUPPORT_TICKETS").document(ticketId)
                .set(ticket)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ticket #" + ticketId + " Raised Successfully!", Toast.LENGTH_LONG).show();
                    sheet.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showCancelConfirmation(TransactionRecord txn, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Request?")
                .setMessage("Do you want to cancel this withdrawal request? Amount will be added back to your balance.")
                .setPositiveButton("YES, CANCEL", (dialog, which) -> cancelWithdrawal(txn, position))
                .setNegativeButton("NO", (dialog, which) -> adapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    private void cancelWithdrawal(TransactionRecord txn, int position) {
        if (mAuth.getCurrentUser() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        String uid = mAuth.getCurrentUser().getUid();
        String txnId = txn.getTransactionId();

        DocumentReference boyRef = db.collection("delivery_boy").document(uid);
        DocumentReference txnRef = boyRef.collection("transactions").document(txnId);
        DocumentReference globalRef = db.collection("withdrawals").document(txnId);

        db.runTransaction(transaction -> {
            // Check status again inside transaction
            DocumentSnapshot snapshot = transaction.get(txnRef);
            String currentStatus = snapshot.getString("status");
            
            if ("Pending".equalsIgnoreCase(currentStatus)) {
                // Restore balance
                transaction.update(boyRef, "main balance", FieldValue.increment(txn.getAmount()));
                // Update boy record status
                transaction.update(txnRef, "status", "Cancelled");
                transaction.update(txnRef, "cancelDate", FieldValue.serverTimestamp());
                // Delete global request
                transaction.delete(globalRef);
                return true;
            }
            return false;
        }).addOnSuccessListener(success -> {
            progressBar.setVisibility(View.GONE);
            if (success) {
                Toast.makeText(this, "Request Cancelled & Balance Restored!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not cancel. Status might have changed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            adapter.notifyItemChanged(position);
        });
    }

    private void loadTransactions() {
        if (mAuth.getCurrentUser() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("delivery_boy").document(mAuth.getCurrentUser().getUid())
                .collection("transactions")
                .orderBy("requestDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        transactionList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            TransactionRecord record = doc.toObject(TransactionRecord.class);
                            if (record != null) {
                                record.setTransactionId(doc.getId());
                                transactionList.add(record);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        noTxnText.setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}