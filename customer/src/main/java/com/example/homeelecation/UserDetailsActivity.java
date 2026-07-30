package com.example.homeelecation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {

    private EditText fullName, email;
    private RadioGroup genderGroup;
    private Button submitButton;
    private ProgressBar progressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        fullName = findViewById(R.id.user_details_full_name);
        email = findViewById(R.id.user_details_email);
        genderGroup = findViewById(R.id.user_details_gender_group);
        submitButton = findViewById(R.id.user_details_submit_button);
        progressBar = findViewById(R.id.user_details_progressBar);

        progressBar.setVisibility(View.GONE);

        submitButton.setOnClickListener(v -> saveUserDetails());
    }

    private void saveUserDetails() {
        String name = fullName.getText().toString().trim();
        String emailAddress = email.getText().toString().trim();
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();

        if (name.isEmpty()) {
            fullName.setError("Full name is required");
            fullName.requestFocus();
            return;
        }

        if (emailAddress.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender;
        if (selectedGenderId == R.id.gender_male) {
            gender = "Male";
        } else if (selectedGenderId == R.id.gender_female) {
            gender = "Female";
        } else {
            gender = "Other";
        }

        setProgressBar(true);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            setProgressBar(false);
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserDetailsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        String uid = currentUser.getUid();
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, Object> user = new HashMap<>();
        user.put("Full Name", name);
        user.put("email", emailAddress);
        user.put("gender", gender);
        user.put("mobile", currentUser.getPhoneNumber());
        user.put("created_at", FieldValue.serverTimestamp());
        user.put("last_device_id", androidId);

        db.collection("USER").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    registerDevice(uid, androidId);
                })
                .addOnFailureListener(e -> {
                    setProgressBar(false);
                    Toast.makeText(UserDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void registerDevice(String uid, String deviceId) {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("device_name", getDeviceName());
        deviceMap.put("login_at", FieldValue.serverTimestamp());
        deviceMap.put("device_id", deviceId);

        db.collection("USER").document(uid)
                .collection("DEVICES").document(deviceId)
                .set(deviceMap)
                .addOnCompleteListener(task -> {
                    setProgressBar(false);
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(UserDetailsActivity.this, HomeActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserDetailsActivity.this, "Device registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            submitButton.setEnabled(false);
            submitButton.setAlpha(0.5f);
        } else {
            progressBar.setVisibility(View.GONE);
            submitButton.setEnabled(true);
            submitButton.setAlpha(1.0f);
        }
    }
    
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model.startsWith(manufacturer) ? capitalize(model) : capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        char first = s.charAt(0);
        return Character.isUpperCase(first) ? s : Character.toUpperCase(first) + s.substring(1);
    }
}
