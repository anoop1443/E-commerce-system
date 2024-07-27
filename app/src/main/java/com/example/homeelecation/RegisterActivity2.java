package com.example.homeelecation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity2 extends AppCompatActivity {

    private EditText fullName,phoneNumber,pin;
    private Button register;
    private TextView login;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        fullName = findViewById(R.id.register_activity_FullName);
        phoneNumber = findViewById(R.id.register_activity_Phone_number);
       // pin = findViewById(R.id.register_activity_Password);
        register = findViewById(R.id.register_activity_Button);
        login = findViewById(R.id.User_login);
        progressBar = findViewById(R.id.register_activity_progressBar);

        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backLogin = new Intent(RegisterActivity2.this,MainActivity.class);
                startActivity(backLogin);
                finish();
            }
        });
        //new math

        FirebaseApp.initializeApp(RegisterActivity2.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fullName.getText().toString().isEmpty()) {

                    if (!phoneNumber.getText().toString().trim().isEmpty()) {

                        if (phoneNumber.getText().toString().trim().length() == 10) {
                            setProgressBar(true);

                            PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder()
                                    .setPhoneNumber("+91" + phoneNumber.getText().toString())
                                    .setTimeout(60l, TimeUnit.SECONDS)
                                    .setActivity(RegisterActivity2.this)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                           }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {

                                            Toast.makeText(RegisterActivity2.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            phoneNumber.setError("number invalided");
                                            setProgressBar(false);

                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                            Intent intent = new Intent(RegisterActivity2.this, VerificationActivity2.class);
                                            intent.putExtra("mobile", phoneNumber.getText().toString());
                                            intent.putExtra("sentOTP", s);
                                            intent.putExtra("Name", fullName.getText().toString());
                                            setProgressBar(false);
                                            startActivity(intent);

                                        }

                                    });
                            PhoneAuthProvider.verifyPhoneNumber(builder.build());


                        } else {

                            phoneNumber.setError("please Enter current number");
                            //Toast.makeText(RegisterActivity2.this, "please Enter current number", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        phoneNumber.setError("please Enter Mobile Number");
                       // Toast.makeText(RegisterActivity2.this, "please Enter Name and Mobile number", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    fullName.setError("please Enter Name");
                    //Toast.makeText(RegisterActivity2.this,"please Enter Name",Toast.LENGTH_LONG).show();
                }
            }
        });
       // new math


        // deeper
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!fullName.getText().toString().isEmpty()) {
//
//
//                    if (!phoneNumber.getText().toString().trim().isEmpty()) {
//                            if ((phoneNumber.getText().toString().trim()).length() == 10) {
//
//
//                                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber.getText().toString(), 30, TimeUnit.SECONDS,
//                                        RegisterActivity2.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                            @Override
//                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                               // FirebaseUser currentUser = mAuth.getCurrentUser();
//                                                //updateUI(currentUser);
//
//                                            }
//
//                                            @Override
//                                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                                // getotpbt.setVisibility(View.GONE);
//
//                                                Toast.makeText(RegisterActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                            }
//
//                                            @Override
//                                            public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                                super.onCodeSent(backendotp, forceResendingToken);
////                                                Intent intent = new Intent(RegisterActivity2.this, VerificationActivity2.class);
////                                                intent.putExtra("mobile", inputphone.getText().toString());
////                                                intent.putExtra("backendotp", backendotp);
////                                                startActivity(intent);
//
//                                                Intent intent2 = new Intent(RegisterActivity2.this, VerificationActivity2.class);
//                                                intent2.putExtra("mobile", phoneNumber.getText().toString());
//                                                intent2.putExtra("sentOTP", backendotp);
//                                                intent2.putExtra("Name", fullName.getText().toString());
//                                                setProgressBar(false);
//                                                startActivity(intent2);
//
//
//                                            }
//                                        }
//                                );
//
//
//                            } else {
//                                Toast.makeText(RegisterActivity2.this, "please enter corret mobile", Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            Toast.makeText(RegisterActivity2.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
//                        }
//                }else {
//                    fullName.setError("please Enter Name");
//                    //Toast.makeText(RegisterActivity2.this,"please Enter Name",Toast.LENGTH_LONG).show();
//                }
//
//
//            }
//        });
        // deeper

    }

    private void setProgressBar(boolean check){

        if (check){
            progressBar.setVisibility(View.VISIBLE);
            register.setClickable(false);

        }else {
            progressBar.setVisibility(View.GONE);
            register.setClickable(true);



        }

    }

//    protected void onStart() {
//        super.onStart();
//
//        if (mAuth.getCurrentUser() != null) {
//            Intent intent = new Intent(this, HomeActivity2.class);
//            startActivity(intent);
//            finish();
//        }
//
//    }



}