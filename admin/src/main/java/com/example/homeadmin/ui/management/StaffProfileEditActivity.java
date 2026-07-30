package com.example.homeadmin.ui.management;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffProfileEditActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView uidText;
    private EditText nameInput, mobileInput;
    private Spinner roleSpinner, statusSpinner;
    private Button saveBtn;
    private ProgressBar progressBar;

    private String uid, currentRole;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }

        profileImage = findViewById(R.id.editProfileImage);
        uidText = findViewById(R.id.editUidText);
        nameInput = findViewById(R.id.editNameInput);
        mobileInput = findViewById(R.id.editMobileInput);
        roleSpinner = findViewById(R.id.editRoleSpinner);
        statusSpinner = findViewById(R.id.editStatusSpinner);
        saveBtn = findViewById(R.id.saveProfileBtn);
        progressBar = findViewById(R.id.editProfileProgressBar);

        db = FirebaseFirestore.getInstance();

        // Get data from intent
        uid = getIntent().getStringExtra("uid");
        String name = getIntent().getStringExtra("name");
        String mobile = getIntent().getStringExtra("mobile");
        currentRole = getIntent().getStringExtra("role");
        String status = getIntent().getStringExtra("status");
        String image = getIntent().getStringExtra("image");

        uidText.setText("UID: " + uid);
        nameInput.setText(name);
        mobileInput.setText(mobile);

        Glide.with(this)
                .load(image)
                .apply(new RequestOptions().placeholder(R.drawable.ic_person))
                .into(profileImage);

        setupSpinners(currentRole, status);

        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void setupSpinners(String currentRole, String status) {
        String[] roles = {"Customer", "Electrician Boy", "Admin"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        // Set selection
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equals(currentRole)) {
                roleSpinner.setSelection(i);
                break;
            }
        }

        String[] statuses = {"Active", "Inactive", "Offline"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        // Set selection
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                statusSpinner.setSelection(i);
                break;
            }
        }
    }

    private void saveProfile() {
        String newName = nameInput.getText().toString().trim();
        String newMobile = mobileInput.getText().toString().trim();
        String newRole = roleSpinner.getSelectedItem().toString();
        String newStatus = statusSpinner.getSelectedItem().toString();

        if (newName.isEmpty() || newMobile.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("Full Name", newName);
        updateMap.put("mobile", newMobile);
        updateMap.put("role", newRole);
        updateMap.put("status", newStatus);

        // Determine collection based on role (current or new)
        String collection = (currentRole != null && (currentRole.equals("Delivery Boy") || currentRole.equals("Admin"))) ? "UsersAdmin" : "USER";

        db.collection(collection).document(uid)
                .update(updateMap)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    saveBtn.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(StaffProfileEditActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(StaffProfileEditActivity.this, "Update Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
