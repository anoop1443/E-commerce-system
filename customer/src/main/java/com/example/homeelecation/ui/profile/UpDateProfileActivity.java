package com.example.homeelecation.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpDateProfileActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private ImageView genderImageView;
    private EditText fullNameEditText, emailEditText;
    private Button updateButton;
    private Dialog loadingDialog;
    private String selectedGender = "Male";
    private FirebaseFirestore firebaseFirestore;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_up_date_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize UI
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        genderImageView = findViewById(R.id.upDateImage);
        fullNameEditText = findViewById(R.id.upDate_fullName);
        emailEditText = findViewById(R.id.upDate_email);
        updateButton = findViewById(R.id.update_btn);

        // Loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Get Data from Intent or Static Class
        String intentName = getIntent().getStringExtra("NAME");
        String intentEmail = getIntent().getStringExtra("EMAIL");

        String currentName = (intentName != null) ? intentName : DbLoadData.fullName;
        String currentEmail = (intentEmail != null) ? intentEmail : DbLoadData.email;

        fullNameEditText.setText(currentName);
        emailEditText.setText(currentEmail);
        
        // Handle Gender Selection
        if (DbLoadData.gender != null && DbLoadData.gender.equalsIgnoreCase("Female")) {
            femaleRadioButton.setChecked(true);
            selectedGender = "Female";
            genderImageView.setImageResource(R.drawable.female_avatar);
        } else {
            maleRadioButton.setChecked(true);
            selectedGender = "Male";
            genderImageView.setImageResource(R.drawable.male_avatar);
        }

        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.maleRadioButton) {
                selectedGender = "Male";
                genderImageView.setImageResource(R.drawable.male_avatar);
            } else if (checkedId == R.id.femaleRadioButton) {
                selectedGender = "Female";
                genderImageView.setImageResource(R.drawable.female_avatar);
            }
        });

        updateButton.setOnClickListener(v -> validateAndUpdate());
    }

    private void validateAndUpdate() {
        String name = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim().toLowerCase();

        if (TextUtils.isEmpty(name)) {
            fullNameEditText.setError("Enter Full Name");
            fullNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !email.matches(EMAIL_REGEX)) {
            emailEditText.setError("Enter a valid Email");
            emailEditText.requestFocus();
            return;
        }

        loadingDialog.show();
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("Full Name", name);
        updateMap.put("email", email);
        updateMap.put("gender", selectedGender);

        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
                .update(updateMap)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        DbLoadData.fullName = name;
                        DbLoadData.email = email;
                        DbLoadData.gender = selectedGender;
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Update Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
