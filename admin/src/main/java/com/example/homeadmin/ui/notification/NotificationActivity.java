package com.example.homeadmin.ui.notification;

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

//import com.example.homeelecation.HomeActivity2;
//import com.example.homeelecation.R;
//import com.example.homeelecation.ui.DbLoadData;
import com.example.homeadmin.HomeActivity2;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.google.firebase.auth.FirebaseAuth;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Notification");
        

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        recyclerView = findViewById(R.id.notification_recycleView);
        button = findViewById(R.id.activity_notifi_button);
        textView = findViewById(R.id.activity_notifi_textView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        notificationAdapter = new NotificationAdapter(DbLoadData.notificationModelList);


        Map<String,Object> readMap = new HashMap<>();

        for (int x = 0; x < DbLoadData.notificationModelList.size(); x++){
            if (!DbLoadData.notificationModelList.get(x).isRead()){
                runQuery = true;
            }
            readMap.put("Read_"+x,true);
        }

        if (runQuery){
            DbLoadData.firebaseFirestore.collection("USER")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").
                    document("MY_NOTIFICATIONS")
                    .update(readMap);

        }

        if (DbLoadData.notificationModelList.size() == 0){
            textView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(notificationAdapter);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, HomeActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();


            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int x = 0; x < DbLoadData.notificationModelList.size(); x++){
            DbLoadData.notificationModelList.get(x).setRead(true);
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)

            finish();

//
        return super.onOptionsItemSelected(item);


    }

}