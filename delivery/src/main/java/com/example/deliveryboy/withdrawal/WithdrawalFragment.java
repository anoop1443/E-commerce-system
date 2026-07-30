package com.example.deliveryboy.withdrawal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

public class WithdrawalFragment extends Fragment {

    private TextView balanceTextView;
    private EditText amountEditText;
    private Button requestButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WithdrawalRequestAdapter adapter;
    private List<WithdrawalRequest> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double currentBalance;
    private ListenerRegistration balanceListener;

    public WithdrawalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdrawal, container, false);

        balanceTextView = view.findViewById(R.id.textViewBalance);
        amountEditText = view.findViewById(R.id.editTextWithdrawalAmount);
        requestButton = view.findViewById(R.id.buttonRequestWithdrawal);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerViewWithdrawalHistory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList = new ArrayList<>();
        adapter = new WithdrawalRequestAdapter(getContext(), requestList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        // Check if user is logged in and fetch data
        if (mAuth.getCurrentUser() != null) {
            fetchWithdrawalHistory(mAuth.getCurrentUser().getUid());
           // fetchWithdrawalHistory();
        } else {
            // If no user is logged in, you'd typically handle this with a login flow.
            // For this example, we'll just show a message.
            Toast.makeText(getContext(), "User not authenticated. Please log in.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
//            emptyStateText.setVisibility(View.VISIBLE);
//            emptyStateText.setText("Please log in to see your history.");
        }

        fetchBalance();

        requestButton.setOnClickListener(v -> requestWithdrawal());

        return view;
    }

    private void fetchBalance() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String deliveryBoyId = user.getUid();
        DocumentReference docRef = db.collection("delivery_boy").document(deliveryBoyId);

        progressBar.setVisibility(View.VISIBLE);
        balanceListener = docRef.addSnapshotListener((documentSnapshot, e) -> {
            progressBar.setVisibility(View.GONE);
            if (e != null) {
                Log.w("WithdrawalFragment", "Listen failed.", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Double balance = documentSnapshot.getDouble("main balance");
                if (balance != null) {
                    currentBalance = balance;
                    balanceTextView.setText("₹ " + String.format("%.2f", currentBalance));
                }
            } else {
                Toast.makeText(getContext(), "Balance data not found.", Toast.LENGTH_SHORT).show();
                currentBalance = 0;
                balanceTextView.setText("₹ 0.00");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (balanceListener != null) {
            balanceListener.remove();
        }
    }

    private void fetchWithdrawalHistory(String deliveryBoyId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();

            return;
        }


        db.collection("withdrawal_requests")
                .whereEqualTo("deliveryBoyId",deliveryBoyId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        WithdrawalRequest request = document.toObject(WithdrawalRequest.class);
                        request.setDocumentId(document.getId());
                        request.setDeliveryBoyId(document.getString("deliveryBoyId"));
                        request.setAmount(document.getDouble("amount"));
                        request.setStatus(document.getString("status"));
                        request.setTimestamp(document.getTimestamp("timestamp"));
                        requestList.add(request);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch history.", Toast.LENGTH_SHORT).show();
                });
    }

    private void requestWithdrawal() {
        String amountString = amountEditText.getText().toString().trim();
        if (amountString.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        double requestedAmount = Double.parseDouble(amountString);
        if (requestedAmount <= 0) {
            Toast.makeText(getContext(), "Amount must be greater than zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestedAmount > currentBalance) {
            Toast.makeText(getContext(), "Requested amount is more than your current balance.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> withdrawalRequest = new HashMap<>();
        withdrawalRequest.put("deliveryBoyId", user.getUid());
        withdrawalRequest.put("amount", requestedAmount);
        withdrawalRequest.put("timestamp", FieldValue.serverTimestamp());
        withdrawalRequest.put("status", "pending");

        db.collection("withdrawal_requests")
                .add(withdrawalRequest)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Withdrawal request sent successfully!", Toast.LENGTH_SHORT).show();
                    amountEditText.setText("");
                    fetchWithdrawalHistory(user.getUid()); // History को update करें
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to send withdrawal request.", Toast.LENGTH_SHORT).show();
                });
    }
}
