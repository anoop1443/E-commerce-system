package com.example.homeadmin.ui.quickServices;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.homeadmin.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddQuickServiceActivity extends AppCompatActivity {

    private EditText etName, etPrice, etCategory, etDesc, etRules, etIndex, etColor;
    private CheckBox cbAvailable;
    private Button btnSave, btnSelectIcon;
    private ImageView iconPreview;
    private TextView pageTitle;
    
    private String serviceId;
    private String iconUrl = "";
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Dialog loadingDialog;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        startCrop(imageUri);
                    }
                }
            }
    );

    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher = registerForActivityResult(
            new CropImageContract(),
            result -> {
                if (result.isSuccessful()) {
                    Uri croppedImageUri = result.getUriContent();
                    if (croppedImageUri != null) {
                        uploadImageToStorage(croppedImageUri);
                    }
                } else {
                    Exception error = result.getError();
                    if (error != null) {
                        Toast.makeText(this, "Crop failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quick_service);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        serviceId = getIntent().getStringExtra("SERVICE_ID");

        // UI Initialization
        pageTitle = findViewById(R.id.page_title);
        iconPreview = findViewById(R.id.service_icon_preview);
        btnSelectIcon = findViewById(R.id.select_icon_btn);
        etName = findViewById(R.id.et_service_name);
        etPrice = findViewById(R.id.et_service_price);
        etCategory = findViewById(R.id.et_service_category);
        etDesc = findViewById(R.id.et_service_desc);
        etRules = findViewById(R.id.et_service_rules);
        etIndex = findViewById(R.id.et_service_index);
        etColor = findViewById(R.id.et_service_color);
        cbAvailable = findViewById(R.id.cb_available);
        btnSave = findViewById(R.id.save_service_btn);

        // Loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (serviceId != null) {
            pageTitle.setText("Edit Quick Service");
            loadServiceDetails();
        }

        btnSelectIcon.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveService());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void startCrop(Uri imageUri) {
        CropImageOptions options = new CropImageOptions();
        options.guidelines = CropImageView.Guidelines.ON;
        options.aspectRatioX = 1;
        options.aspectRatioY = 1;
        options.fixAspectRatio = true;
        options.outputCompressFormat = Bitmap.CompressFormat.JPEG;
        options.activityTitle = "Crop Image";
        options.cropMenuCropButtonTitle = "Done";
        
        cropImageLauncher.launch(new CropImageContractOptions(imageUri, options));
    }

    private void uploadImageToStorage(Uri uri) {
        loadingDialog.show();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            // Compression
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference ref = storage.getReference().child("quick_service_icons/" + fileName);

            ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    loadingDialog.dismiss();
                    iconUrl = downloadUri.toString();
                    Glide.with(this).load(iconUrl).into(iconPreview);
                    Toast.makeText(this, "Icon Uploaded", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadServiceDetails() {
        loadingDialog.show();
        db.collection("QUICK_SERVICES").document(serviceId).get().addOnSuccessListener(doc -> {
            loadingDialog.dismiss();
            if (doc.exists()) {
                etName.setText(doc.getString("name"));
                etPrice.setText(doc.getString("price"));
                etCategory.setText(doc.getString("category"));
                etDesc.setText(doc.getString("description"));
                etRules.setText(doc.getString("rules"));
                etIndex.setText(String.valueOf(doc.getLong("index")));
                etColor.setText(doc.getString("color"));
                cbAvailable.setChecked(doc.getBoolean("available"));
                iconUrl = doc.getString("icon");
                if (iconUrl != null && !iconUrl.isEmpty()) {
                    Glide.with(this).load(iconUrl).placeholder(R.drawable.ic_home).into(iconPreview);
                }
            }
        });
    }

    private void saveService() {
        String name = etName.getText().toString();
        String price = etPrice.getText().toString();
        String category = etCategory.getText().toString();
        String desc = etDesc.getText().toString();
        String rules = etRules.getText().toString();
        String indexStr = etIndex.getText().toString();
        String color = etColor.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(indexStr)) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("category", category);
        data.put("description", desc);
        data.put("rules", rules);
        data.put("index", Integer.parseInt(indexStr));
        data.put("available", cbAvailable.isChecked());
        data.put("icon", iconUrl);
        data.put("color", color.isEmpty() ? "#FFFFFF" : color);
        data.put("dateTime", FieldValue.serverTimestamp());

        if (serviceId == null) {
            db.collection("QUICK_SERVICES").add(data).addOnSuccessListener(ref -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            db.collection("QUICK_SERVICES").document(serviceId).set(data).addOnSuccessListener(aVoid -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
