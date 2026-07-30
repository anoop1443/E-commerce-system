package com.example.deliveryboy.account;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliveryboy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SupportTicketsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SupportTicketAdapter adapter;
    private List<Map<String, Object>> ticketList;
    private ProgressBar progressBar;
    private TextView noTicketsText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_tickets);

         currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        Toolbar toolbar = findViewById(R.id.tickets_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.ticketsRecyclerView);
        progressBar = findViewById(R.id.ticketsProgressBar);
        noTicketsText = findViewById(R.id.noTicketsText);

        ticketList = new ArrayList<>();
        adapter = new SupportTicketAdapter(this, ticketList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadTickets();
    }

    private void loadTickets() {
        if (mAuth.getCurrentUser() == null) return;

        progressBar.setVisibility(View.VISIBLE);


        db.collection("SUPPORT_TICKETS")
                .whereEqualTo("deliveryBoyId", mAuth.getCurrentUser().getUid())
                .orderBy("createdDate", Query.Direction.DESCENDING)
                  .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        ticketList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Map<String, Object> ticket = doc.getData();
                            if (ticket != null) {
                                ticketList.add(ticket);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        noTicketsText.setVisibility(ticketList.isEmpty() ? View.VISIBLE : View.GONE);
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