package com.example.homeadmin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton,register;
    private ProgressBar progressBar;

    private Dialog resetPasswordDialog; // Added for the new feature
    private TextView forgotPasswordTextView; // Added for the new feature

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get references to UI elements
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView); // Initialize the new TextView
        register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this,AdminRegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        // Setup for the new Forgot Password feature
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });
        // Set up the login button click listener
        if (mAuth.getCurrentUser() != null) {
            checkAdminStatus(mAuth.getCurrentUser().getUid());
        }else {

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginAdmin();
                }
            });
        }
    }

    private void loginAdmin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check for empty fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar and disable login button
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Authenticate with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, now check for admin status
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkAdminStatus(user.getUid());
                            } else {
                                // This should not happen, but as a safeguard
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                loginButton.setEnabled(true);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            loginButton.setEnabled(true);
                        }
                    }
                });
    }

    private void checkAdminStatus(String userId){
            db.collection("UsersAdmin").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            progressBar.setVisibility(View.GONE);
                            loginButton.setEnabled(true);

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Check if the user is an admin
                                    Boolean isAdmin = document.getBoolean("isAdmin");
                                    if (isAdmin != null && isAdmin) {
                                        // User is an admin, register device and navigate
                                        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                        registerDevice(userId, androidId);
                                    } else {
                                        // User is not an admin
                                        Toast.makeText(MainActivity.this, "Access denied. You are not an admin.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut(); // Sign out the non-admin user
                                        progressBar.setVisibility(View.GONE);
                                        loginButton.setEnabled(true);
                                    }
                                } else {
                                    // User document does not exist
                                    Toast.makeText(MainActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut(); // Sign out the unknown user
                                    progressBar.setVisibility(View.GONE);
                                    loginButton.setEnabled(true);
                                }
                            } else {
                                // Error getting document
                                Toast.makeText(MainActivity.this, "Error checking user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mAuth.signOut(); // Sign out due to error
                                progressBar.setVisibility(View.GONE);
                                loginButton.setEnabled(true);
                            }
                        }
                    });
    }

    // New method to show the password reset dialog
    private void showResetPasswordDialog() {
        if (resetPasswordDialog == null) {
            resetPasswordDialog = new Dialog(this);
            resetPasswordDialog.setContentView(R.layout.dialog_forgot_password);
            resetPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            resetPasswordDialog.setCancelable(true);
        }

        final EditText emailInput = resetPasswordDialog.findViewById(R.id.editTextEmail);
        final Button resetButton = resetPasswordDialog.findViewById(R.id.buttonResetPassword);
        final ProgressBar resetProgressBar = resetPasswordDialog.findViewById(R.id.progressBar);

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            resetProgressBar.setVisibility(View.VISIBLE);
            resetButton.setEnabled(false);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        resetProgressBar.setVisibility(View.GONE);
                        resetButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                            resetPasswordDialog.dismiss();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        resetPasswordDialog.show();
    }

    private void registerDevice(String uid, String deviceId) {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("device_name", getDeviceName());
        deviceMap.put("login_at", FieldValue.serverTimestamp());
        deviceMap.put("device_id", deviceId);

        db.collection("UsersAdmin").document(uid)
                .collection("DEVICES").document(deviceId)
                .set(deviceMap)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Admin login successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Device registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model == null) return manufacturer != null ? manufacturer : "Unknown Device";
        if (manufacturer == null) return model;
        return model.startsWith(manufacturer) ? capitalize(model) : capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        char first = s.charAt(0);
        return Character.isUpperCase(first) ? s : Character.toUpperCase(first) + s.substring(1);
    }

    @SuppressLint("HardwareIds")
    public void logoutCurrentDevice() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore.getInstance().collection("UsersAdmin").document(uid)
                .collection("DEVICES").document(androidId)
                .delete()
                .addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    public void logoutFromAllDevices() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("UsersAdmin").document(uid)
                .collection("DEVICES").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot ds : task.getResult()) {
                            ds.getReference().delete();
                        }
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}