package com.example.homeelecation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;

public class VerificationActivity2 extends AppCompatActivity {

    private EditText enterOTP;
    private Button submit;
    private TextView resendOtp, resendTimer;
    private String oneTimeOTP, fullName, mobileNumber;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification2);

        // UI Initializing
        TextView setPhone = findViewById(R.id.verification_activity_setPhone);
        enterOTP = findViewById(R.id.verification_activity_Otp);
        submit = findViewById(R.id.verification_activity_submitBt);
        progressBar = findViewById(R.id.verification_activity_progressBar);
        resendOtp = findViewById(R.id.verification_activity_resendOtp);
        resendTimer = findViewById(R.id.resend_timer_textview);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Getting Data from LoginActivity with Safety
        Intent intent = getIntent();
        if (intent != null) {
            mobileNumber = intent.getStringExtra("mobile");
            oneTimeOTP = intent.getStringExtra("sentOTP");
            fullName = intent.getStringExtra("Name");
        }

        if (mobileNumber == null || oneTimeOTP == null) {
            Toast.makeText(this, "Verification data missing. Try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (setPhone != null) {
            setPhone.setText("+91 " + mobileNumber);
        }

        startTimer();

        if (submit != null) {
            submit.setOnClickListener(v -> {
                String code = enterOTP.getText().toString().trim();
                if (code.length() == 6) {
                    verifyOTP(code);
                } else {
                    enterOTP.setError("Valid OTP required length 6");
                }
            });
        }

        if (resendOtp != null) {
            resendOtp.setOnClickListener(v -> {
                if (!resendOtp.isEnabled()) return;
                resendOTP();
            });
        }
    }

    private void startTimer() {
        if (resendOtp != null) resendOtp.setEnabled(false);
        if (resendTimer != null) {
            resendTimer.setVisibility(View.VISIBLE);

            if (countDownTimer != null) countDownTimer.cancel();
            
            countDownTimer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    resendTimer.setText("Resend OTP in " + millisUntilFinished / 1000 + "s");
                }

                public void onFinish() {
                    resendOtp.setEnabled(true);
                    resendTimer.setVisibility(View.GONE);
                }
            }.start();
        }
    }

    private void resendOTP() {
        setProgressBar(true);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mobileNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        setProgressBar(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        setProgressBar(false);
                        Toast.makeText(VerificationActivity2.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        setProgressBar(false);
                        oneTimeOTP = verificationId;
                        startTimer();
                        Toast.makeText(VerificationActivity2.this, "OTP Resent Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String code) {
        if (oneTimeOTP == null) {
            Toast.makeText(this, "OTP ID missing. Resend OTP.", Toast.LENGTH_SHORT).show();
            return;
        }
        setProgressBar(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(oneTimeOTP, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserInFirestore();
                    } else {
                        setProgressBar(false);
                        if (enterOTP != null) enterOTP.setError("Invalid OTP");
                        Toast.makeText(VerificationActivity2.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("HardwareIds")
    private void checkUserInFirestore() {
        if (mAuth.getCurrentUser() == null) {
            setProgressBar(false);
            Toast.makeText(this, "Authentication failed. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String uid = mAuth.getCurrentUser().getUid();
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        DocumentReference docRef = firebaseFirestore.collection("USER").document(uid);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Toast.makeText(VerificationActivity2.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                    registerDevice(uid, androidId);
                } else {
                    setProgressBar(false);
                    Intent intent = new Intent(VerificationActivity2.this, UserDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {
                setProgressBar(false);
                String err = task.getException() != null ? task.getException().getMessage() : "Unknown DB Error";
                Toast.makeText(VerificationActivity2.this, "Database Error: " + err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewUser(String uid) {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, Object> userData = new HashMap<>();
        userData.put("full Name", fullName);
        userData.put("mobile", "+91" + mobileNumber);
        userData.put("email", "");
        userData.put("last_device_id", androidId);
        userData.put("profile_image", "");
        userData.put("created_at", FieldValue.serverTimestamp());
        userData.put("device_name", getDeviceName());

        firebaseFirestore.collection("USER").document(uid)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        registerDevice(uid, androidId);
                    } else {
                        setProgressBar(false);
                        Toast.makeText(VerificationActivity2.this, "Failed to create user profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setProgressBar(boolean check) {
        if (progressBar != null) progressBar.setVisibility(check ? View.VISIBLE : View.GONE);
        if (submit != null) submit.setEnabled(!check);
    }

    public void mainIntent() {
        Intent intent = new Intent(VerificationActivity2.this, HomeActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
    private void registerDevice(String uid, String deviceId) {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("device_name", getDeviceName());
        deviceMap.put("login_at", FieldValue.serverTimestamp());
        deviceMap.put("device_id", deviceId);

        firebaseFirestore.collection("USER").document(uid)
                .collection("DEVICES").document(deviceId)
                .set(deviceMap)
                .addOnCompleteListener(task -> {
                    setProgressBar(false);
                    if (task.isSuccessful()) {
                        mainIntent();
                    } else {
                        Toast.makeText(VerificationActivity2.this, "Device registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("HardwareIds")
    public void logoutCurrentDevice() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore.getInstance().collection("USER").document(uid)
                .collection("DEVICES").document(androidId)
                .delete()
                .addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(VerificationActivity2.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    @SuppressLint("HardwareIds")
    public void logoutFromAllDevices() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("USER").document(uid)
                .collection("DEVICES").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot ds : task.getResult()) {
                            ds.getReference().delete();
                        }
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(VerificationActivity2.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
