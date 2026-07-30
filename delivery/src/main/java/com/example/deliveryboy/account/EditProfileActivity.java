package com.example.deliveryboy.account;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.deliveryboy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, bankNameEdit, accNoEdit, ifscEdit, holderNameEdit;
    private Button saveButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEdit = findViewById(R.id.edit_name);
        emailEdit = findViewById(R.id.edit_email);
        bankNameEdit = findViewById(R.id.edit_bank_name);
        accNoEdit = findViewById(R.id.edit_acc_no);
        ifscEdit = findViewById(R.id.edit_ifsc);
        holderNameEdit = findViewById(R.id.edit_holder_name);
        saveButton = findViewById(R.id.btn_save_profile);
        progressBar = findViewById(R.id.edit_profile_loader);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadCurrentData();

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("delivery_boy").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);
                    if (doc.exists()) {
                        nameEdit.setText(doc.getString("name"));
                        emailEdit.setText(doc.getString("email"));
                        bankNameEdit.setText(doc.getString("bankName"));
                        accNoEdit.setText(doc.getString("accountNumber"));
                        ifscEdit.setText(doc.getString("ifscCode"));
                        holderNameEdit.setText(doc.getString("holderName"));
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfile() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String bank = bankNameEdit.getText().toString().trim();
        String acc = accNoEdit.getText().toString().trim();
        String ifsc = ifscEdit.getText().toString().trim();
        String holder = holderNameEdit.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("bankName", bank);
        updates.put("accountNumber", acc);
        updates.put("ifscCode", ifsc);
        updates.put("holderName", holder);

        db.collection("delivery_boy").document(user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}