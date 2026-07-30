package com.example.homeadmin.user;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextInputEditText nameEdit, emailEdit, mobileEdit;
    private RadioGroup genderGroup;
    private RadioButton maleBtn, femaleBtn;
    private Button updateBtn, changePhotoBtn;
    private ProgressBar progressBar;
    private RecyclerView addressRecyclerView, ordersRecyclerView;

    private String uid;
    private FirebaseFirestore db;
    private Uri newProfileImageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    newProfileImageUri = result.getData().getData();
                    profileImage.setImageURI(newProfileImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        uid = getIntent().getStringExtra("uid");
        db = FirebaseFirestore.getInstance();

        initViews();
        loadUserDetails();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profileImage = findViewById(R.id.user_profile_image);
        nameEdit = findViewById(R.id.edit_user_full_name);
        emailEdit = findViewById(R.id.edit_user_email);
        mobileEdit = findViewById(R.id.edit_user_mobile);
        genderGroup = findViewById(R.id.radio_group_gender);
        maleBtn = findViewById(R.id.radio_male);
        femaleBtn = findViewById(R.id.radio_female);
        updateBtn = findViewById(R.id.btn_update_profile);
        changePhotoBtn = findViewById(R.id.btn_change_photo);
        progressBar = findViewById(R.id.profile_progress_bar);
        addressRecyclerView = findViewById(R.id.recycler_view_addresses);
        ordersRecyclerView = findViewById(R.id.recycler_view_orders);

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        changePhotoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        updateBtn.setOnClickListener(v -> updateProfile());
    }

    private void loadUserDetails() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("USER").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                nameEdit.setText(doc.getString("Full Name"));
                emailEdit.setText(doc.getString("email"));
                mobileEdit.setText(doc.getString("mobile"));
                String gender = doc.getString("gender");
                if ("Male".equalsIgnoreCase(gender)) maleBtn.setChecked(true);
                else if ("Female".equalsIgnoreCase(gender)) femaleBtn.setChecked(true);

                Glide.with(this).load(doc.getString("profile image")).placeholder(R.drawable.ic_person).into(profileImage);

                loadAddresses();
                loadOrders();
            } else {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void loadAddresses() {
        db.collection("USER").document(uid).collection("MY_ADDRESSES").get().addOnSuccessListener(snapshots -> {
            List<String> addresses = new ArrayList<>();
            for (DocumentSnapshot doc : snapshots) {
                addresses.add(formatAddress(doc));
            }
            addressRecyclerView.setAdapter(new SimpleTextAdapter(addresses));
        });
    }

    private void loadOrders() {
        db.collection("USER").document(uid).collection("USER_ORDERS").limit(5).get().addOnSuccessListener(snapshots -> {
            List<String> orderIds = new ArrayList<>();
            for (DocumentSnapshot doc : snapshots) {
                orderIds.add(doc.getString("orderID"));
            }
            if (!orderIds.isEmpty()) {
                db.collection("ORDERS").whereIn(FieldPath.documentId(), orderIds).get().addOnSuccessListener(orderSnaps -> {
                   List<String> orderSummaries = new ArrayList<>();
                   for (DocumentSnapshot doc : orderSnaps) {
                       orderSummaries.add("Order ID: " + doc.getId() + "\nStatus: " + doc.getString("Order Status"));
                   }
                   ordersRecyclerView.setAdapter(new SimpleTextAdapter(orderSummaries));
                });
            }
        });
    }

    private String formatAddress(DocumentSnapshot doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.getString("fullname") != null) sb.append(doc.getString("fullname")).append("\n");
        if (doc.getString("house") != null) sb.append(doc.getString("house")).append(", ");
        if (doc.getString("area") != null) sb.append(doc.getString("area")).append("\n");
        if (doc.getString("city") != null) sb.append(doc.getString("city")).append(", ");
        if (doc.getString("state") != null) sb.append(doc.getString("state")).append(" - ");
        if (doc.getString("pincode") != null) sb.append(doc.getString("pincode")).append("\n");
        if (doc.getString("mobile") != null) sb.append("Mobile: ").append(doc.getString("mobile"));
        
        Boolean selected = doc.getBoolean("selected");
        if (selected != null && selected) {
            sb.append(" (Selected)");
        }
        return sb.toString();
    }

    private void updateProfile() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String mobile = mobileEdit.getText().toString().trim();
        String gender = maleBtn.isChecked() ? "Male" : "Female";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        updateBtn.setEnabled(false);

        if (newProfileImageUri != null) {
            uploadProfileImage(name, email, mobile, gender);
        } else {
            saveToFirestore(name, email, mobile, gender, null);
        }
    }

    private void uploadProfileImage(String name, String email, String mobile, String gender) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("user_profile_images/" + uid + ".png");
        ref.putFile(newProfileImageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(name, email, mobile, gender, uri.toString());
                });
            } else {
                progressBar.setVisibility(View.GONE);
                updateBtn.setEnabled(true);
                Toast.makeText(this, "Photo upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirestore(String name, String email, String mobile, String gender, String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("Full Name", name);
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("gender", gender);
        if (imageUrl != null) {
            map.put("profile image", imageUrl);
        }

        db.collection("USER").document(uid).update(map).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            updateBtn.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    private static class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {
        private final List<String> items;

        public SimpleTextAdapter(List<String> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(32, 16, 32, 16);
            tv.setTextColor(Color.BLACK);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) { super(itemView); }
        }
    }
}
