package com.example.homeadmin.ui.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatsFragment extends Fragment {

    private TextView totalCustomersText, activeBoysText, openTicketsText;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        totalCustomersText = view.findViewById(R.id.totalCustomersText);
        activeBoysText = view.findViewById(R.id.activeBoysText);
        openTicketsText = view.findViewById(R.id.openTicketsText);
        Button manageTicketsBtn = view.findViewById(R.id.manageTicketsBtn);

        db = FirebaseFirestore.getInstance();

        loadStats();

        manageTicketsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SupportManagementActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadStats() {
        // Customers count
        db.collection("USER").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                totalCustomersText.setText(String.valueOf(task.getResult().size()));
            }
        });

        // Active Delivery Boys count
        db.collection("delivery_boy")
                .whereEqualTo("status", "Active")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                activeBoysText.setText(String.valueOf(task.getResult().size()));
            }
        });

        // Support Tickets count
        db.collection("SUPPORT_TICKETS")
                .whereEqualTo("status", "Open")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                openTicketsText.setText(String.valueOf(task.getResult().size()));
            }
        });
    }
}
