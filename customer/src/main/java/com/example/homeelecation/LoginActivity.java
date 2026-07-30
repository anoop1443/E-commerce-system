package com.example.homeelecation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private Button loginButton;
    private Button skipButton;
    private ProgressBar progressBar;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = findViewById(R.id.login_phone_number);
        loginButton = findViewById(R.id.login_button);
        skipButton = findViewById(R.id.skip_button);
        progressBar = findViewById(R.id.login_progressBar);
        loginButton.setText("Get OTP");

        progressBar.setVisibility(View.GONE);


        loginButton.setOnClickListener(v -> {
            String mobile = phoneNumber.getText().toString().trim();

            if (mobile.isEmpty() || mobile.length() != 10) {
                phoneNumber.setError("Please enter a valid 10-digit number");
                phoneNumber.requestFocus();
                return;
            }

            sendOTP(mobile);
        });

        skipButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void sendOTP(String mobile) {
        setProgressBar(true);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mobile)
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
                        Toast.makeText(LoginActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        setProgressBar(false);
                        Intent intent = new Intent(LoginActivity.this, VerificationActivity2.class);
                        intent.putExtra("mobile", mobile);
                        intent.putExtra("sentOTP", verificationId);
                        startActivity(intent);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setProgressBar(boolean check) {
        if (check) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            loginButton.setAlpha(0.5f);
            skipButton.setEnabled(false);
            skipButton.setAlpha(0.5f);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setAlpha(1.0f);
            skipButton.setEnabled(true);
            skipButton.setAlpha(1.0f);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
