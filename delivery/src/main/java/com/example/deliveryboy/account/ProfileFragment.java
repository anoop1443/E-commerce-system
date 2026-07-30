package com.example.deliveryboy.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deliveryboy.R;
import com.example.deliveryboy.earning.EarningHistoryActivity;
import com.example.deliveryboy.orderfech.Order;
import com.example.deliveryboy.withdrawal.TransactionHistoryActivity;
import com.example.deliveryboy.withdrawal.WithdrawalActivity;
import com.example.deliveryboy.account.ViewProfileActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView nameTextView, phoneTextView, balanceTextView;
    private TextView totalTasksTextView, totalEarningsTextView, successRateTextView;
    private MaterialSwitch onlineStatusSwitch;
    private Button withdrawButton;
    private ImageButton logoutButton;
    private View earningHistoryCard, viewProfileCard;
    private View menuTransactions, menuHelp, menuTerms, menuLogout;
    private RecyclerView orderHistoryRecyclerView;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration profileListener, earningsListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Views को initialize करें
        profileImageView = view.findViewById(R.id.profileImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        balanceTextView = view.findViewById(R.id.balanceTextView);
        
        totalTasksTextView = view.findViewById(R.id.totalTasksTextView);
        totalEarningsTextView = view.findViewById(R.id.totalEarningsTextView);
        successRateTextView = view.findViewById(R.id.successRateTextView);
        
        onlineStatusSwitch = view.findViewById(R.id.onlineStatusSwitch);
        withdrawButton = view.findViewById(R.id.withdrawButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        
        earningHistoryCard = view.findViewById(R.id.earningHistoryButton_Card);
        viewProfileCard = view.findViewById(R.id.viewProfileButton_Card);

        menuTransactions = view.findViewById(R.id.menu_transactions);
        menuHelp = view.findViewById(R.id.menu_help);
        menuTerms = view.findViewById(R.id.menu_terms);
        menuLogout = view.findViewById(R.id.menu_logout);
        
        orderHistoryRecyclerView = view.findViewById(R.id.orderHistoryRecyclerView);

        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(getContext(), orderList);
        orderHistoryRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadUserProfile();
        loadEarningsStats();
        loadOrderHistory();

        onlineStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                db.collection("delivery_boy").document(user.getUid())
                        .update("isOnline", isChecked)
                        .addOnFailureListener(e -> {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Failed to update status.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        withdrawButton.setOnClickListener(v -> {
           Intent intent = new Intent(getContext(), WithdrawalActivity.class);
           startActivity(intent);
        });

        earningHistoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EarningHistoryActivity.class);
            startActivity(intent);
        });
        
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        menuTransactions.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
            startActivity(intent);
        });

        menuHelp.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HelpCenterActivity.class);
            startActivity(intent);
        });

        menuTerms.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Terms & Conditions coming soon!", Toast.LENGTH_SHORT).show();
        });

        menuLogout.setOnClickListener(v -> {
            mAuth.signOut();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        
        viewProfileCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ViewProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadEarningsStats() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            earningsListener = db.collection("delivery_boy").document(user.getUid())
                    .collection("earnings_history")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null || snapshots == null || !isAdded()) return;
                        
                        int totalTasks = snapshots.size();
                        double totalEarnings = 0;
                        int successfulTasks = 0;
                        
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Double amount = doc.getDouble("amount");
                            String status = doc.getString("status");
                            
                            if (amount != null) totalEarnings += amount;
                            if ("Delivered".equalsIgnoreCase(status)) successfulTasks++;
                        }
                        
                        totalTasksTextView.setText(String.valueOf(totalTasks));
                        totalEarningsTextView.setText("₹" + (int)totalEarnings);
                        
                        if (totalTasks > 0) {
                            int rate = (successfulTasks * 100) / totalTasks;
                            successRateTextView.setText(rate + "%");
                        } else {
                            successRateTextView.setText("0%");
                        }
                    });
        }
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("delivery_boy").document(user.getUid());
            
            profileListener = docRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null || !isAdded()) {
                    Log.w("ProfileFragment", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String phone = documentSnapshot.getString("phone");
                    String profileUrl = documentSnapshot.getString("profileImage");
                    Double balance = documentSnapshot.getDouble("main balance");
                    Boolean isOnline = documentSnapshot.getBoolean("isOnline");

                    nameTextView.setText(name != null ? name : "Delivery Partner");
                    phoneTextView.setText("+91 " + (phone != null ? phone : "0000000000"));

                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        Glide.with(this).load(profileUrl).circleCrop().into(profileImageView);
                    }

                    if (balance != null) {
                        balanceTextView.setText("₹ " + String.format("%.2f", balance));
                    }
                    if (isOnline != null) {
                        onlineStatusSwitch.setChecked(isOnline);
                    }
                }
            });
        }
    }

    private void loadOrderHistory() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("ORDERS")
                    .whereEqualTo("deliveryBoyID", user.getUid())
                    .whereEqualTo("globalStatus", "Completed")
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!isAdded()) return;
                        orderList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Order order = document.toObject(Order.class);
                            order.setOrderID(document.getId());
                            order.setQuickOrder(false);
                            
                            db.collection("ORDERS").document(order.getOrderID()).collection("orderItems").limit(1).get()
                                    .addOnSuccessListener(items -> {
                                        if (!isAdded()) return;
                                        if (!items.isEmpty()) {
                                            QueryDocumentSnapshot item = (QueryDocumentSnapshot) items.getDocuments().get(0);
                                            order.setProductTitle(item.getString("productTitle"));
                                            order.setImageUrl(item.getString("productImage"));
                                        }
                                        orderList.add(order);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (profileListener != null) profileListener.remove();
        if (earningsListener != null) earningsListener.remove();
    }
}