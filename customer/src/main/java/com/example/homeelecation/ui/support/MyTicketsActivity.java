package com.example.homeelecation.ui.support;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {


    Toolbar toolbar;
    private RecyclerView ticketsRecyclerView;
    private MyTicketsAdapter ticketsAdapter;
    private List<SupportTicketModel> ticketList = new ArrayList<>(); // Ek naya Model banayenge
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_tickets);
        EdgeToEdge.enable(this);

        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.appbar));
        // EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.bottom_action_container));
       // EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.cart_activity_constraint));


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Support Tickets");

        String currentUserId = FirebaseAuth.getInstance().getUid();



        ticketsRecyclerView = findViewById(R.id.RecyclerView_tic);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ticketsRecyclerView.setLayoutManager(layoutManager);

        ticketsAdapter = new MyTicketsAdapter(ticketList,this);
        ticketsRecyclerView.setAdapter(ticketsAdapter);

        if (currentUserId != null) {
            loadUserTickets(new Dialog(this),currentUserId);
            //Toast.makeText(this, " user yes", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "user Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadUserTickets(Dialog loadingDialog, String currentUserId) {
        loadingDialog.show();

        db.collection("SUPPORT_TICKETS")
                .whereEqualTo("userId", currentUserId)
                .orderBy("createdDate", Query.Direction.DESCENDING) // Naye tickets pehle
                .get()
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful() && task.getResult() != null) {
                        ticketList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SupportTicketModel model = document.toObject(SupportTicketModel.class);
                            // ID humne document mein bhi save ki hai, but safety ke liye:
                            if (model.getTicketId() == null) {
                                model.setTicketId(document.getId());
                            }
                            ticketList.add(model);
                        }
                        ticketsAdapter.notifyDataSetChanged();
                        
                        if (ticketList.isEmpty()) {
                            Toast.makeText(this, "No support tickets found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}