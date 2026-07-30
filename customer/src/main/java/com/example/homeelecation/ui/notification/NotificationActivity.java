package com.example.homeelecation.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.HomeActivity2;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    public  static  NotificationAdapter notificationAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button button;
    private TextView textView;
    private  boolean runQuery = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Notification");
        }

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        recyclerView = findViewById(R.id.notification_recycleView);
        button = findViewById(R.id.activity_notifi_button);
        textView = findViewById(R.id.activity_notifi_textView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            notificationAdapter = new NotificationAdapter(DbLoadData.notificationModelList);
            recyclerView.setAdapter(notificationAdapter);
        }


        // --- SUB-COLLECTION READ STATUS UPDATE LOGIC ---
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null && !DbLoadData.notificationModelList.isEmpty()) {
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            
            for (int x = 0; x < DbLoadData.notificationModelList.size(); x++) {
                NotificationModel model = DbLoadData.notificationModelList.get(x);
                if (model != null && !model.isRead()) {
                    runQuery = true;
                    String docId = model.getNotificationId();
                    if (docId != null) {
                        batch.update(FirebaseFirestore.getInstance()
                                .collection("USER").document(uid)
                                .collection("MY_NOTIFICATIONS").document(docId), "Read", true);
                    }
                }
            }

            if (runQuery) {
                batch.commit().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (int x = 0; x < DbLoadData.notificationModelList.size(); x++) {
                            NotificationModel model = DbLoadData.notificationModelList.get(x);
                            if (model != null) model.setRead(true);
                        }
                        if (notificationAdapter != null) {
                            notificationAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }

        // Empty UI Check
        if (DbLoadData.notificationModelList.isEmpty()) {
            if (textView != null) textView.setVisibility(View.VISIBLE);
            if (button != null) button.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        } else {
            if (textView != null) textView.setVisibility(View.GONE);
            if (button != null) button.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        }

        if (button != null) {
            button.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationActivity.this, HomeActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
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
