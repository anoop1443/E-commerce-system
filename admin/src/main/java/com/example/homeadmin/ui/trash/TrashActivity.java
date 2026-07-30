package com.example.homeadmin.ui.trash;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrashActivity extends AppCompatActivity implements TrashAdapter.OnTrashActionListener {

    private RecyclerView recyclerView;
    private TrashAdapter adapter;
    private List<TrashModel> trashList;
    private View emptyState;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.trash_recyclerview);
        emptyState = findViewById(R.id.empty_state);
        progressBar = findViewById(R.id.progressBar);

        trashList = new ArrayList<>();
        adapter = new TrashAdapter(trashList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadTrashItems();
    }

    private void loadTrashItems() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("TRASH")
                .orderBy("deletedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    trashList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        TrashModel item = doc.toObject(TrashModel.class);
                        item.setTrashId(doc.getId());
                        trashList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
    }

    private void updateEmptyState() {
        if (trashList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRestore(TrashModel item, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Restore Item")
                .setMessage("Are you sure you want to restore this " + item.getType().toLowerCase() + "?")
                .setPositiveButton("Restore", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    TrashManager.restoreFromTrash(item, new TrashManager.OnTrashOperationListener() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            trashList.remove(position);
                            adapter.notifyItemRemoved(position);
                            updateEmptyState();
                            Toast.makeText(TrashActivity.this, "Restored successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TrashActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDelete(TrashModel item, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Forever")
                .setMessage("This action cannot be undone. Are you sure you want to permanently delete this " + item.getType().toLowerCase() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    TrashManager.permanentDelete(item, new TrashManager.OnTrashOperationListener() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            trashList.remove(position);
                            adapter.notifyItemRemoved(position);
                            updateEmptyState();
                            Toast.makeText(TrashActivity.this, "Deleted permanently", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TrashActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
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
