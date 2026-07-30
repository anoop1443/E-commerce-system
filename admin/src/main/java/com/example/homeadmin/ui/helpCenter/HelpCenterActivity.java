package com.example.homeadmin.ui.helpCenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCenterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HelpCategoryAdapter adapter;
    private List<HelpCategoryModel> categoryList = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Uri selectedIconUri;
    private ImageView dialogIconView;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedIconUri = result.getData().getData();
                    if (dialogIconView != null) {
                        dialogIconView.setImageURI(selectedIconUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_view_categories);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HelpCategoryAdapter(categoryList, new HelpCategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(HelpCategoryModel category) {
                Intent intent = new Intent(HelpCenterActivity.this, HelpCategoryDetailActivity.class);
                intent.putExtra("categoryId", category.getCategoryId());
                intent.putExtra("categoryName", category.getName());
                startActivity(intent);
            }

            @Override
            public void onEditClick(HelpCategoryModel category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(HelpCategoryModel category) {
                new AlertDialog.Builder(HelpCenterActivity.this)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this category and all its questions?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteCategory(category))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab_add_category).setOnClickListener(v -> showCategoryDialog(null));

        loadCategories();
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("HELP_CENTER")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    categoryList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            HelpCategoryModel category = doc.toObject(HelpCategoryModel.class);
                            categoryList.add(category);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showCategoryDialog(HelpCategoryModel category) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_category);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView title = dialog.findViewById(R.id.dialog_title);
        dialogIconView = dialog.findViewById(R.id.dialog_category_icon);
        Button selectIconBtn = dialog.findViewById(R.id.btn_select_icon);
        TextInputEditText idEdit = dialog.findViewById(R.id.edit_category_id);
        TextInputEditText nameEdit = dialog.findViewById(R.id.edit_category_name);
        TextInputEditText colorEdit = dialog.findViewById(R.id.edit_category_color);
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        Button saveBtn = dialog.findViewById(R.id.btn_save);

        selectedIconUri = null;

        if (category != null) {
            title.setText("Edit Category");
            idEdit.setText(category.getCategoryId());
            idEdit.setEnabled(false);
            nameEdit.setText(category.getName());
            colorEdit.setText(category.getColor());
            if (category.getIcon() != null && !category.getIcon().isEmpty()) {
                Glide.with(this).load(category.getIcon()).into(dialogIconView);
            }
        }

        selectIconBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String id = idEdit.getText().toString().trim();
            String name = nameEdit.getText().toString().trim();
            String color = colorEdit.getText().toString().trim();

            if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(color)) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveBtn.setEnabled(false);
            if (selectedIconUri != null) {
                uploadIcon(id, name, color, dialog);
            } else {
                // If no new icon is selected, use the existing one if editing, or empty string if new
                String existingIcon = (category != null) ? category.getIcon() : "";
                updateFirestore(id, name, color, existingIcon, dialog);
            }
        });

        dialog.show();
    }

    private void uploadIcon(String id, String name, String color, Dialog dialog) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("help_center_icons/" + id + ".png");
        ref.putFile(selectedIconUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    updateFirestore(id, name, color, uri.toString(), dialog);
                });
            } else {
                Toast.makeText(this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                dialog.findViewById(R.id.btn_save).setEnabled(true);
            }
        });
    }

    private void updateFirestore(String id, String name, String color, String iconUrl, Dialog dialog) {
        Map<String, Object> map = new HashMap<>();
        map.put("categoryId", id);
        map.put("name", name);
        map.put("color", color);
        map.put("icon", iconUrl);

        db.collection("HELP_CENTER").document(id).set(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Category saved successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                dialog.findViewById(R.id.btn_save).setEnabled(true);
            }
        });
    }

    private void deleteCategory(HelpCategoryModel category) {
        progressBar.setVisibility(View.VISIBLE);
        // First delete all questions in sub-collection
        db.collection("HELP_CENTER").document(category.getCategoryId())
                .collection("QUESTIONS").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            doc.getReference().delete();
                        }
                        // Then delete the category document
                        db.collection("HELP_CENTER").document(category.getCategoryId()).delete()
                                .addOnCompleteListener(task1 -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Error deleting category", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error deleting questions", Toast.LENGTH_SHORT).show();
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
