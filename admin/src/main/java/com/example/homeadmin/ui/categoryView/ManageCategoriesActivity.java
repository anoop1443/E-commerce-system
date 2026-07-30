package com.example.homeadmin.ui.categoryView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageCategoriesActivity extends AppCompatActivity implements ManageCategoryAdapter.OnCategoryActionListener {

    private RecyclerView recyclerView;
    private ManageCategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    private FirebaseFirestore db;
    private ExtendedFloatingActionButton addFab;
    private Button saveOrderBtn;
    private boolean isOrderChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.manage_categories_recyclerview);
        addFab = findViewById(R.id.add_category_fab);
        saveOrderBtn = findViewById(R.id.save_order_btn);

        categoryList = new ArrayList<>();
        adapter = new ManageCategoryAdapter(categoryList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadCategories();

        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCategoriesActivity.this, AddCategoryActivity.class);
            intent.putExtra("NEXT_INDEX", (long) categoryList.size());
            startActivity(intent);
        });

        saveOrderBtn.setOnClickListener(v -> saveNewOrder());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(categoryList, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                isOrderChanged = true;
                saveOrderBtn.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadCategories() {
        db.collection("CATEGORY").orderBy("index").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoryList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CategoryModel model = document.toObject(CategoryModel.class);
                    model.setCategoryId(document.getId());
                    categoryList.add(model);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewOrder() {
        WriteBatch batch = db.batch();
        for (int i = 0; i < categoryList.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", (long) i);
            batch.update(db.collection("CATEGORY").document(categoryList.get(i).getCategoryId()), map);
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Order saved successfully", Toast.LENGTH_SHORT).show();
                saveOrderBtn.setVisibility(View.GONE);
                isOrderChanged = false;
            } else {
                Toast.makeText(this, "Failed to save order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(CategoryModel category) {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        intent.putExtra("CATEGORY_ID", category.getCategoryId());
        startActivity(intent);
    }

    @Override
    public void onDelete(CategoryModel category, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete '" + category.getCategoryName() + "'? It will be moved to the Recycle Bin for 7 days.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    com.example.homeadmin.ui.trash.TrashManager.moveToTrash(
                            "CATEGORY",
                            category.getCategoryId(),
                            "CATEGORY",
                            category.getCategoryName(),
                            category.getCategoryIconLink(),
                            new com.example.homeadmin.ui.trash.TrashManager.OnTrashOperationListener() {
                                @Override
                                public void onSuccess() {
                                    adapter.removeItem(position);
                                    Toast.makeText(ManageCategoriesActivity.this, "Moved to Recycle Bin", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(ManageCategoriesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
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
