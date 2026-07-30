package com.example.deliveryboy.withdrawal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.deliveryboy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawalActivity extends AppCompatActivity {

    private TextView balanceTextView, selectedBankText, accountNumberMaskedText;
    private EditText amountEditText;
    private Button requestButton, viewHistoryButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double currentBalance;
    private ListenerRegistration balanceListener;
    
    // Saved Bank Details
    private String savedBankName, savedAccNo, savedIfsc, savedHolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        Toolbar toolbar = findViewById(R.id.withdrawal_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        balanceTextView = findViewById(R.id.textViewBalance);
        amountEditText = findViewById(R.id.editTextWithdrawalAmount);
        selectedBankText = findViewById(R.id.textViewSelectedBank);
        accountNumberMaskedText = findViewById(R.id.textViewAccountNumberMasked);
        
        requestButton = findViewById(R.id.buttonRequestWithdrawal);
        viewHistoryButton = findViewById(R.id.buttonViewHistory);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchBalanceAndBankDetails();

        requestButton.setOnClickListener(v -> validateAndRequest());
        viewHistoryButton.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
        });
    }

    private void fetchBalanceAndBankDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("delivery_boy").document(user.getUid())
                .addSnapshotListener((documentSnapshot, e) -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Balance
                        Double balance = documentSnapshot.getDouble("main balance");
                        currentBalance = balance != null ? balance : 0;
                        balanceTextView.setText("₹ " + String.format("%.2f", currentBalance));
                        
                        // Bank Details
                        savedBankName = documentSnapshot.getString("bankName");
                        savedAccNo = documentSnapshot.getString("accountNumber");
                        savedIfsc = documentSnapshot.getString("ifscCode");
                        savedHolderName = documentSnapshot.getString("holderName");
                        
                        if (savedBankName != null && savedAccNo != null) {
                            selectedBankText.setText(savedBankName);
                            accountNumberMaskedText.setText("Acc: " + maskAccount(savedAccNo));
                            requestButton.setEnabled(true);
                        } else {
                            selectedBankText.setText("No Bank Details Found");
                            accountNumberMaskedText.setText("Please update bank info in profile");
                            requestButton.setEnabled(false);
                        }
                    }
                });
    }

    private String maskAccount(String acc) {
        if (acc == null || acc.length() < 4) return "****";
        return "******" + acc.substring(acc.length() - 4);
    }

    private void validateAndRequest() {
        String amountStr = amountEditText.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (savedBankName == null || savedAccNo == null) {
            Toast.makeText(this, "Bank details missing", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount < 100) {
            Toast.makeText(this, "Minimum withdrawal is ₹100", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > currentBalance) {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirm Withdrawal")
                .setMessage("Amount: ₹" + amount + "\nBank: " + savedBankName + "\nAcc: " + maskAccount(savedAccNo))
                .setPositiveButton("PROCEED", (dialog, which) -> submitWithdrawalRequest(amount))
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void submitWithdrawalRequest(double amount) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        requestButton.setEnabled(false);

        String uid = user.getUid();
        DocumentReference boyRef = db.collection("delivery_boy").document(uid);
        
        String withdrawalId = db.collection("withdrawals").document().getId();

        Map<String, Object> txnData = new HashMap<>();
        txnData.put("amount", amount);
        txnData.put("status", "Pending");
        txnData.put("requestDate", FieldValue.serverTimestamp());
        txnData.put("bankName", savedBankName);
        txnData.put("accountNumber", savedAccNo);
        txnData.put("ifscCode", savedIfsc);
        txnData.put("holderName", savedHolderName);
        txnData.put("transactionId", withdrawalId);

        Map<String, Object> globalRequest = new HashMap<>(txnData);
        globalRequest.put("deliveryBoyId", uid);

        db.runTransaction(transaction -> {
            transaction.update(boyRef, "main balance", FieldValue.increment(-amount));
            transaction.set(boyRef.collection("transactions").document(withdrawalId), txnData);
            transaction.set(db.collection("withdrawals").document(withdrawalId), globalRequest);
            return null;
        }).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Withdrawal Request Submitted!", Toast.LENGTH_LONG).show();
            amountEditText.setText("");
            requestButton.setEnabled(true);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            requestButton.setEnabled(true);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (balanceListener != null) balanceListener.remove();
    }
}