package com.example.deliveryboy.earning;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliveryboy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class EarningHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EarningAdapter adapter;
    private List<EarningRecord> earningList;
    private ProgressBar progressBar;
    private TextView noEarningsText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_history);

        recyclerView = findViewById(R.id.earningHistoryRecyclerView);
        progressBar = findViewById(R.id.earningProgressBar);
        noEarningsText = findViewById(R.id.noEarningsText);

        earningList = new ArrayList<>();
        adapter = new EarningAdapter(earningList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadEarningHistory();
    }

    private void loadEarningHistory() {
        if (mAuth.getCurrentUser() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("delivery_boy").document(mAuth.getCurrentUser().getUid())
                .collection("earnings_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    earningList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        EarningRecord record = doc.toObject(EarningRecord.class);
                        if (record != null) {
                            earningList.add(record);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    noEarningsText.setVisibility(earningList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
