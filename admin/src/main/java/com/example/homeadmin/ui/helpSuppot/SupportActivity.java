package com.example.homeadmin.ui.helpSuppot;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<SupportTicketModel> ticketList;
    private MyTicketsAdapter ticketsAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_support);

        recyclerView = findViewById(R.id.RecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseFirestore.getInstance();
        ticketList = new ArrayList<>();
        ticketsAdapter = new MyTicketsAdapter(ticketList, this);
        recyclerView.setAdapter(ticketsAdapter);

        loadUserTickets(new Dialog(this));

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }



    private void loadUserTickets(Dialog loadingDialog) {
        loadingDialog.show();

        db.collection("SUPPORT_TICKETS")// Sirf current user ke tickets
                .orderBy("timestamp", Query.Direction.DESCENDING) // Naye tickets upar
                .get()
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful() && task.getResult() != null) {
                        ticketList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Toast.makeText(this, "Result yes"+ document, Toast.LENGTH_SHORT).show();
                            // Document ko Model mein convert karo
                            SupportTicketModel model = document.toObject(SupportTicketModel.class);
                            model.setTicketId(document.getId());// ID save kar lo;

                            ticketList.add(model);
                            ticketsAdapter.notifyDataSetChanged();
                        }
                        // Agar list khali hai to "No Tickets Found" message dikhao
                    } else {
                        Toast.makeText(this, "Failed to load tickets.", Toast.LENGTH_SHORT).show();
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