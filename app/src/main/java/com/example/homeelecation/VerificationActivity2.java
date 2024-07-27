package com.example.homeelecation;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VerificationActivity2 extends AppCompatActivity {

    private TextView setPhone, resentOTP;
    private EditText enterOTP;
    private Button submit;

    private String comeOTP, comeName, phone;

    private ProgressBar progressBar;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private DocumentReference docRefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification2);

        setPhone = findViewById(R.id.verification_activity_setPhone);
        resentOTP = findViewById(R.id.verification_activity_resendOtp);
        enterOTP = findViewById(R.id.verification_activity_Otp);
        submit = findViewById(R.id.verification_activity_submitBt);
        progressBar = findViewById(R.id.verification_activity_progressBar);

        progressBar.setVisibility(View.GONE);

        setPhone.setText(String.format("+91" + getIntent().getStringExtra("mobile")));
        phone = String.format("+91" + getIntent().getStringExtra("mobile"));
        comeOTP = getIntent().getStringExtra("sentOTP");
        comeName = getIntent().getStringExtra("Name");

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(VerificationActivity2.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!enterOTP.getText().toString().trim().isEmpty()) {
                    String EnterCodOTP = enterOTP.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    if (comeOTP != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                comeOTP, EnterCodOTP
                        );


                        mAuth.signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            DocumentReference DocRef = FirebaseFirestore.getInstance().collection("USER")
                                                    .document(mAuth.getCurrentUser().getUid());
                                            DocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot doc = task.getResult();

                                                        if (doc.exists()) {

                                                            Toast.makeText(VerificationActivity2.this, "WelCome Back", Toast.LENGTH_SHORT).show();
                                                            maiIntent();
                                                            setProgressBar(false);

                                                        } else {

                                                            Toast.makeText(VerificationActivity2.this, "Login ", Toast.LENGTH_SHORT).show();

                                                            Map<String, Object> userUpdate = new HashMap<>();
                                                            userUpdate.put("Full Name", comeName);


                                                            firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
                                                                    .set(userUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                                CollectionReference userDataReference = firebaseFirestore.collection("USER")
                                                                                        .document(FirebaseAuth.getInstance().getUid())
                                                                                        .collection("USER_DATA");

                                                                                ///Map
                                                                                Map<String, Object> wishlistMap = new HashMap<>();
                                                                                wishlistMap.put("list_size", (long) 0);

                                                                                Map<String, Object> ratingMap = new HashMap<>();
                                                                                ratingMap.put("list_size", (long) 0);

                                                                                Map<String, Object> cartMap = new HashMap<>();
                                                                                cartMap.put("list_size", (long) 0);

                                                                                Map<String, Object> addressesMap = new HashMap<>();
                                                                                addressesMap.put("list_size", (long) 0);
                                                                                ///Map

                                                                                List<String> documentNames = new ArrayList<>();
                                                                                documentNames.add("MY_WISHLIST");
                                                                                documentNames.add("MY_RATINGS");
                                                                                documentNames.add("MY_CART");
                                                                                documentNames.add("MY_ADDRESSES");

                                                                                List<Map<String, Object>> documentFliedNames = new ArrayList<>();
                                                                                documentFliedNames.add(wishlistMap);
                                                                                documentFliedNames.add(ratingMap);
                                                                                documentFliedNames.add(cartMap);
                                                                                documentFliedNames.add(addressesMap);

                                                                                for (int x = 0; x < documentNames.size(); x++) {

                                                                                    int finalX = x;
                                                                                    userDataReference.document(documentNames.get(x))
                                                                                            .set(documentFliedNames.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {


                                                                                                    if (task.isSuccessful()) {

                                                                                                        if (finalX == documentNames.size() - 1) {

                                                                                                            maiIntent();
                                                                                                        }

                                                                                                    } else {

                                                                                                        String error = task.getException().getMessage();
                                                                                                        Toast.makeText(VerificationActivity2.this, error, Toast.LENGTH_SHORT).show();
                                                                                                        setProgressBar(false);

                                                                                                    }

                                                                                                }
                                                                                            });
                                                                                }


//                                                                                Map<Object, Long> listSize = new HashMap<>();
//                                                                                listSize.put("list_size", (long) 0);
//
//                                                                                firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
//                                                                                        .collection("USER_DATA").document("MY_WISHLIST")
//                                                                                        .set(listSize).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                                if (task.isSuccessful()) {
//
//                                                                                                    maiIntent();
//
//                                                                                                } else {
//
//                                                                                                    String error = task.getException().getMessage();
//                                                                                                    Toast.makeText(VerificationActivity2.this, error, Toast.LENGTH_SHORT).show();
//                                                                                                    setProgressBar(false);
//
//                                                                                                }
//
//                                                                                            }
//                                                                                        });

                                                                            } else {

                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(VerificationActivity2.this, error, Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                    });


                                                        }

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(VerificationActivity2.this, error, Toast.LENGTH_SHORT).show();
                                                        Log.w(TAG, "Error getting documents.", task.getException());
                                                        // Toast.makeText(VerificationActivity2.this, "not", Toast.LENGTH_SHORT).show();
                                                        setProgressBar(false);

                                                    }
                                                }
                                            });


//
//                                            Map<Object,String> userUpdate = new HashMap<>();
//                                            userUpdate.put("Full Name",comeName);
//
//                                            firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
//                                                    .set(userUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()){
//                                                                Map<Object,Long> listSize = new HashMap<>();
//                                                                listSize.put("list_size",(long)0);
//                                                                firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
//                                                                        .collection("USER_DATA").document("MY_WISHLIST")
//                                                                        .set(listSize).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                if (task.isSuccessful()){
//
//                                                                                    MaiIntent();
//
//                                                                                }else {
//
//                                                                                    String error = task.getException().getMessage();
//                                                                                    Toast.makeText(VerificationActivity2.this, error, Toast.LENGTH_SHORT).show();
//                                                                                    progressBar.setVisibility(View.GONE);
//
//                                                                                }
//
//                                                                            }
//                                                                        });
//
//
//
//
//                                                            }else {
//
//                                                                String error = task.getException().getMessage();
//                                                                Toast.makeText(VerificationActivity2.this, error, Toast.LENGTH_SHORT).show();
//
//                                                            }
//                                                        }
//                                                    });


                                        } else {
                                            enterOTP.setError("please Enter the correct OTP");
                                            Toast.makeText(VerificationActivity2.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
                                            setProgressBar(false);


                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(VerificationActivity2.this, "please check internet connection", Toast.LENGTH_SHORT).show();
                        setProgressBar(false);

                    }

                } else {
                    enterOTP.setError("please Enter OTP number");

                    //Toast.makeText(VerificationActivity2.this, "please enter all number", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void setProgressBar(boolean check) {

        if (check) {
            progressBar.setVisibility(View.VISIBLE);
            submit.setClickable(false);

        } else {
            progressBar.setVisibility(View.GONE);
            submit.setClickable(true);


        }

    }


    public void maiIntent() {
        Intent intent = new Intent(VerificationActivity2.this, HomeActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(VerificationActivity2.this, "suc current", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);


    }

}