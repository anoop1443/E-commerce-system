package com.example.deliveryboy.account;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.deliveryboy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView nameText, idText, btnEdit;
    private View phoneRow, emailRow, bankNameRow, accNoRow, ifscRow;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Toolbar toolbar = findViewById(R.id.view_profile_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        profileImage = findViewById(R.id.view_profile_image);
        nameText = findViewById(R.id.view_profile_name);
        idText = findViewById(R.id.view_profile_id);
        btnEdit = findViewById(R.id.btn_edit_profile);
        progressBar = findViewById(R.id.view_profile_loader);

        phoneRow = findViewById(R.id.info_phone);
        emailRow = findViewById(R.id.info_email);
        bankNameRow = findViewById(R.id.info_bank_name);
        accNoRow = findViewById(R.id.info_acc_no);
        ifscRow = findViewById(R.id.info_ifsc);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupRows();
        loadProfileData();

        btnEdit.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, EditProfileActivity.class));
        });
    }

    private void setupRows() {
        setRow(phoneRow, "Phone", "Loading...");
        setRow(emailRow, "Email", "Loading...");
        setRow(bankNameRow, "Bank Name", "N/A");
        setRow(accNoRow, "Account No", "N/A");
        setRow(ifscRow, "IFSC Code", "N/A");
    }

    private void setRow(View row, String label, String value) {
        ((TextView) row.findViewById(R.id.info_label)).setText(label);
        ((TextView) row.findViewById(R.id.info_value)).setText(value);
    }

    private void loadProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("delivery_boy").document(user.getUid())
                .addSnapshotListener((doc, e) -> {
                    progressBar.setVisibility(View.GONE);
                    if (e != null) {
                        Log.w("ViewProfile", "Listen failed.", e);
                        return;
                    }

                    if (doc != null && doc.exists()) {
                        String name = doc.getString("name");
                        String phone = doc.getString("phone");
                        String email = doc.getString("email");
                        String imageUrl = doc.getString("profileImage");
                        String bank = doc.getString("bankName");
                        String acc = doc.getString("accountNumber");
                        String ifsc = doc.getString("ifscCode");

                        nameText.setText(name != null ? name : "Delivery Partner");
                        idText.setText("ID: #" + user.getUid().substring(0, 8).toUpperCase());
                        
                        setRow(phoneRow, "Phone", phone != null ? "+91 " + phone : "N/A");
                        setRow(emailRow, "Email", email != null ? email : "N/A");
                        setRow(bankNameRow, "Bank Name", bank != null ? bank : "Not Linked");
                        setRow(accNoRow, "Account No", acc != null ? maskAccount(acc) : "Not Linked");
                        setRow(ifscRow, "IFSC Code", ifsc != null ? ifsc : "Not Linked");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this).load(imageUrl).circleCrop().into(profileImage);
                        }
                    }
                });
    }

    private String maskAccount(String acc) {
        if (acc == null || acc.length() < 4) return "Not Linked";
        return "******" + acc.substring(acc.length() - 4);
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