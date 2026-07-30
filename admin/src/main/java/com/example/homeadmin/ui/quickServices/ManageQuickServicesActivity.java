package com.example.homeadmin.ui.quickServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ManageQuickServicesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuickServiceManageAdapter adapter;
    private List<QuickServiceModel> serviceList;
    private FirebaseFirestore db;
    private Button addServiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quick_services);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.quick_services_recyclerview);
        addServiceBtn = findViewById(R.id.add_service_btn);

        serviceList = new ArrayList<>();
        adapter = new QuickServiceManageAdapter(serviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addServiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManageQuickServicesActivity.this, AddQuickServiceActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadServices();
    }

    private void loadServices() {
        db.collection("QUICK_SERVICES").orderBy("index", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    serviceList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        QuickServiceModel model = doc.toObject(QuickServiceModel.class);
                        if (model != null) {
                            model.setId(doc.getId());
                            serviceList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
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
