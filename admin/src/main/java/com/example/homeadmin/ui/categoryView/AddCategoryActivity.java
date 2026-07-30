package com.example.homeadmin.ui.categoryView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnSave, btnUploadIcon;
    private ImageView iconPreview;
    
    private String categoryId;
    private String iconUrl = "";
    private long currentIndex = 0;
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
        setContentView(R.layout.activity_add_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        categoryId = getIntent().getStringExtra("CATEGORY_ID");

        etName = findViewById(R.id.et_category_name);
        btnSave = findViewById(R.id.save_category_btn);
        btnUploadIcon = findViewById(R.id.upload_icon_btn);
        iconPreview = findViewById(R.id.category_icon_preview);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (categoryId != null) {
            getSupportActionBar().setTitle("Edit Category");
            loadCategoryDetails();
        } else {
            currentIndex = getIntent().getLongExtra("NEXT_INDEX", 0);
        }

        btnUploadIcon.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveCategory());
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
        options.activityTitle = "Crop Icon";
        options.cropMenuCropButtonTitle = "Done";
        
        cropImageLauncher.launch(new CropImageContractOptions(imageUri, options));
    }

    private void uploadImageToStorage(Uri uri) {
        loadingDialog.show();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference ref = storage.getReference().child("category_icons/" + fileName);

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

    private void loadCategoryDetails() {
        loadingDialog.show();
        db.collection("CATEGORY").document(categoryId).get().addOnSuccessListener(doc -> {
            loadingDialog.dismiss();
            if (doc.exists()) {
                etName.setText(doc.getString("categoryName"));
                iconUrl = doc.getString("icon");
                currentIndex = doc.getLong("index") != null ? doc.getLong("index") : 0;
                if (iconUrl != null && !iconUrl.isEmpty()) {
                    Glide.with(this).load(iconUrl).placeholder(R.drawable.ic_home).into(iconPreview);
                }
            }
        });
    }

    private void saveCategory() {
        String name = etName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(iconUrl)) {
            Toast.makeText(this, "Please upload a category icon", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("categoryName", name);
        data.put("icon", iconUrl);
        data.put("index", currentIndex);

        if (categoryId == null) {
            db.collection("CATEGORY").add(data).addOnSuccessListener(ref -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            db.collection("CATEGORY").document(categoryId).set(data).addOnSuccessListener(aVoid -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Category Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
