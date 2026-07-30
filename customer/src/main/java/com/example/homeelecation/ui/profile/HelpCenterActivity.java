package com.example.homeelecation.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.support.MyTicketsActivity;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HelpCenterActivity extends AppCompatActivity {

    private RecyclerView categoryRecycler;
    private List<HelpCategoryModel> categoryList;
    private HelpCategoryAdapter adapter;
    private CardView myTicketsBtn;
    private Button emailBtn, callBtn;

    @Inject
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        EdgeToEdge.enable(this);

        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.app_bar_help));


        Toolbar toolbar = findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        myTicketsBtn = findViewById(R.id.btn_my_tickets);
        categoryRecycler = findViewById(R.id.help_categories_recycler);
        emailBtn = findViewById(R.id.btn_email_support);
        callBtn = findViewById(R.id.btn_call_support);

        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        categoryList = new ArrayList<>();
        adapter = new HelpCategoryAdapter(categoryList);
        categoryRecycler.setAdapter(adapter);

        myTicketsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MyTicketsActivity.class));
        });

        emailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@homeelecation.com"));
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        callBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+918288943143"));
            startActivity(intent);
        });

        loadCategories();
    }

    private void loadCategories() {
        db.collection("HELP_CENTER")
                .orderBy("name")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HelpCategoryModel model = document.toObject(HelpCategoryModel.class);
                            categoryList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load help categories", Toast.LENGTH_SHORT).show();
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
