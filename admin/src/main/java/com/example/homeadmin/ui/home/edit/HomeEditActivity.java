package com.example.homeadmin.ui.home.edit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.categoryView.ManageCategoriesActivity;
import com.example.homeadmin.ui.slideshow.edit.AddAdActivity;
import com.example.homeadmin.ui.slideshow.edit.AddBannerActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeEditActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Spinner categoryTargetSpinner;
    private List<String> categoryNames = new ArrayList<>();
    private List<String> categoryIds = new ArrayList<>();
    private String selectedTarget = "HOMEPAGE"; // Default target
    private String selectedCategoryId = null; // Default category ID

    private CardView bannerCard, adCard, horizontalCard, gridCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_edit);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        categoryTargetSpinner = findViewById(R.id.category_target_spinner);
        bannerCard = findViewById(R.id.banner_card);
        adCard = findViewById(R.id.ad_card);
        horizontalCard = findViewById(R.id.horizontal_card);
        gridCard = findViewById(R.id.grid_card);

        setupCardClickListeners();
        loadTargetCategories();
    }

    private void setupCardClickListeners() {
        bannerCard.setOnClickListener(v -> openEditScreen(0));
        adCard.setOnClickListener(v -> openEditScreen(1));
        horizontalCard.setOnClickListener(v -> openEditScreen(2));
        gridCard.setOnClickListener(v -> openEditScreen(3));
    }

    private void openEditScreen(int type) {
        Intent intent = new Intent(this, CategoryEditContainerActivity.class);
        intent.putExtra("EDIT_TYPE", type);
        intent.putExtra("TARGET", selectedTarget);
        intent.putExtra("CATEGORY_ID", selectedCategoryId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add_banner) {
            Intent intent = new Intent(this, AddBannerActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_ad) {
            Intent intent = new Intent(this, AddAdActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_manage_layout) {
            Intent intent = new Intent(this, ManageHomeActivity.class);
            intent.putExtra("TARGET", selectedTarget);
            intent.putExtra("CATEGORY_ID", selectedCategoryId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_manage_categories) {
            Intent intent = new Intent(this, ManageCategoriesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTargetCategories() {
        categoryNames.clear();
        categoryIds.clear();
        categoryNames.add("Home Page");
        categoryIds.add(null); // No ID for Home Page

        db.collection("CATEGORY").orderBy("index").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categoryNames.add(document.getString("categoryName"));
                    categoryIds.add(document.getId());
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categoryTargetSpinner.setAdapter(spinnerAdapter);

                categoryTargetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            selectedTarget = "HOMEPAGE";
                            selectedCategoryId = null;
                        } else {
                            selectedTarget = "CATEGORY_ACTIVITY";
                            selectedCategoryId = categoryIds.get(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedTarget = "HOMEPAGE";
                        selectedCategoryId = null;
                    }
                });

            } else {
                Log.w("FirestoreError", "Error getting categories.", task.getException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTargetCategories();
    }
}
