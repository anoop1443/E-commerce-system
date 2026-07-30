package com.example.homeadmin.ui.slideshow.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddBannerActivity extends AppCompatActivity implements ColorPaletteAdapter.OnColorSelectedListener {

    private EditText bannerColorEditText, productIdEditText, categoryNameEditText;
    private Button addBannerButton;
    private RecyclerView bannersRecyclerView, colorPaletteRecyclerView;
    private CardView selectImageCard;
    private ImageView selectedImageView;
    private View selectImagePlaceholder, previewBackgroundFrame;
    private TextView bannerCountText;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri selectedImageUri;
    private BannersAdapter bannersAdapter;
    private List<DocumentSnapshot> bannerList;
    private ColorPaletteAdapter colorPaletteAdapter;
    private List<Integer> colorPalette;
    private DocumentSnapshot editingBanner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banner);

        Toolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Manage Banners");

        bannerColorEditText = findViewById(R.id.banner_color_edittext);
        productIdEditText = findViewById(R.id.product_id_edittext);
        categoryNameEditText = findViewById(R.id.category_name_edittext);
        addBannerButton = findViewById(R.id.add_banner_button);
        bannersRecyclerView = findViewById(R.id.banners_recyclerview);
        selectImageCard = findViewById(R.id.select_image_card);
        selectedImageView = findViewById(R.id.selected_image_view);
        selectImagePlaceholder = findViewById(R.id.select_image_placeholder);
        previewBackgroundFrame = findViewById(R.id.preview_background_frame);
        bannerCountText = findViewById(R.id.banner_count_text);
        colorPaletteRecyclerView = findViewById(R.id.color_palette_recyclerview);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        bannerList = new ArrayList<>();
        bannersAdapter = new BannersAdapter(bannerList);
        bannersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bannersRecyclerView.setAdapter(bannersAdapter);

        colorPalette = new ArrayList<>();
        colorPaletteAdapter = new ColorPaletteAdapter(colorPalette, this);
        colorPaletteRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        colorPaletteRecyclerView.setAdapter(colorPaletteAdapter);

        addBannerButton.setOnClickListener(v -> {
            if (editingBanner != null) {
                updateBannerInFirestore();
            } else {
                addBannerToFirestore();
            }
        });
        selectImageCard.setOnClickListener(v -> selectImage());

        bannerColorEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    previewBackgroundFrame.setBackgroundColor(Color.parseColor(s.toString()));
                } catch (Exception ignored) {}
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        fetchBannersFromFirestore();
    }

    private void selectImage() {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.guidelines = CropImageView.Guidelines.ON;
        // Banners ke liye rectangular aspect ratio set kar rahe hain (e.g., 2:1)
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.aspectRatioX = 2;
        cropImageOptions.aspectRatioY = 1;

        CropImageContractOptions options = new CropImageContractOptions(null, cropImageOptions);
        cropImageLauncher.launch(options);
    }

    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher = registerForActivityResult(
            new CropImageContract(),
            result -> {
                if (result.isSuccessful()) {
                    selectedImageUri = result.getUriContent();
                    Glide.with(this).load(selectedImageUri).into(selectedImageView);
                    selectImagePlaceholder.setVisibility(View.GONE);
                    createPaletteAsync(selectedImageUri);
                } else {
                    Exception error = result.getError();
                    if (error != null) {
                        Log.e("AddBannerActivity", "Crop error: ", error);
                        Toast.makeText(this, "Crop failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // imagePickerLauncher ki ab zaroorat nahi hai kyunki CropImage automatically picker handle karta hai
    // Par ise rehne dete hain for safety or replace with empty
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {});

    private void createPaletteAsync(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            generatePaletteFromBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatePaletteFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                colorPalette.clear();
                colorPalette.add(palette.getVibrantColor(0x000000));
                colorPalette.add(palette.getLightVibrantColor(0x000000));
                colorPalette.add(palette.getDarkVibrantColor(0x000000));
                colorPalette.add(palette.getMutedColor(0x000000));
                colorPalette.add(palette.getLightMutedColor(0x000000));
                colorPalette.add(palette.getDarkMutedColor(0x000000));
                colorPaletteAdapter.notifyDataSetChanged();

                if (editingBanner == null && !colorPalette.isEmpty()) {
                    onColorSelected(colorPalette.get(0));
                }
            }
        });
    }

    @Override
    public void onColorSelected(int color) {
        bannerColorEditText.setText(String.format("#%06X", (0xFFFFFF & color)));
    }

    private void addBannerToFirestore() {
        String color = bannerColorEditText.getText().toString().trim();
        String categoryName = categoryNameEditText.getText().toString().trim();
        if (selectedImageUri == null || color.isEmpty()) {
            Toast.makeText(this, "Please select an image and enter a color.", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = "banners/";
        if (!categoryName.isEmpty()) {
            path += categoryName + "/";
        }
        String fileName = System.currentTimeMillis() + ".jpg";

        StorageReference storageRef = storage.getReference().child(path + fileName);
        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    saveBannerDataToFirestore(uri.toString(), color);
                }))
                .addOnFailureListener(e -> {
                    Log.e("AddBannerActivity", "Upload failed", e);
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveBannerDataToFirestore(String imageUrl, String backgroundColor) {
        String productId = productIdEditText.getText().toString().trim();
        String categoryName = categoryNameEditText.getText().toString().trim();

        Map<String, Object> bannerData = new HashMap<>();
        bannerData.put("imageUrl", imageUrl);
        bannerData.put("backgroundColor", backgroundColor);
        bannerData.put("dateTime", com.google.firebase.firestore.FieldValue.serverTimestamp());

        if (!productId.isEmpty()) {
            bannerData.put("productID", productId);
        }
        if (!categoryName.isEmpty()) {
            bannerData.put("categoryName", categoryName);
        }

        db.collection("banners").add(bannerData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Banner added successfully!", Toast.LENGTH_SHORT).show();
                    resetForm();
                    fetchBannersFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding banner.", Toast.LENGTH_SHORT).show());
    }

    private void updateBannerInFirestore() {
        String color = bannerColorEditText.getText().toString().trim();
        String categoryName = categoryNameEditText.getText().toString().trim();
        if (editingBanner == null) return;

        Map<String, Object> bannerData = new HashMap<>();
        bannerData.put("backgroundColor", color);
        bannerData.put("productID", productIdEditText.getText().toString().trim());
        bannerData.put("categoryName", categoryName);
        bannerData.put("dateTime", FieldValue.serverTimestamp());

        if (selectedImageUri != null) {
            // Delete old image first if possible
            String oldImageUrl = editingBanner.getString("imageUrl");
            if (oldImageUrl != null && oldImageUrl.contains("firebase")) {
                try {
                    StorageReference oldRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                    oldRef.delete();
                } catch (Exception ignored) {}
            }

            String path = "banners/";
            if (!categoryName.isEmpty()) {
                path += categoryName + "/";
            }
            String fileName = System.currentTimeMillis() + ".jpg";

            StorageReference storageRef = storage.getReference().child(path + fileName);
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        bannerData.put("imageUrl", uri.toString());
                        db.collection("banners").document(editingBanner.getId()).update(bannerData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Banner updated successfully!", Toast.LENGTH_SHORT).show();
                                    resetForm();
                                    fetchBannersFromFirestore();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error updating banner.", Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("banners").document(editingBanner.getId()).update(bannerData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Banner updated successfully!", Toast.LENGTH_SHORT).show();
                        resetForm();
                        fetchBannersFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating banner.", Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchBannersFromFirestore() {
        db.collection("banners").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bannerList.clear();
                    bannerList.addAll(queryDocumentSnapshots.getDocuments());
                    bannersAdapter.notifyDataSetChanged();
                    if (bannerCountText != null) {
                        bannerCountText.setText(bannerList.size() + " Banners");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching banners.", Toast.LENGTH_SHORT).show());
    }

    private void removeBannerFromFirestore(String documentId) {
        db.collection("banners").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
            String imageUrl = documentSnapshot.getString("imageUrl");
            String productId = documentSnapshot.getString("productID");
            String label = "Banner" + (productId != null ? " (Product: " + productId + ")" : "");

            com.example.homeadmin.ui.trash.TrashManager.moveToTrash(
                    "banners",
                    documentId,
                    "BANNER",
                    label,
                    imageUrl,
                    new com.example.homeadmin.ui.trash.TrashManager.OnTrashOperationListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AddBannerActivity.this, "Moved to Recycle Bin", Toast.LENGTH_SHORT).show();
                            fetchBannersFromFirestore();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(AddBannerActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    private void resetForm() {
        editingBanner = null;
        bannerColorEditText.setText("");
        productIdEditText.setText("");
        categoryNameEditText.setText("");
        selectedImageView.setImageDrawable(null);
        selectImagePlaceholder.setVisibility(View.VISIBLE);
        previewBackgroundFrame.setBackgroundColor(Color.parseColor("#EEEEEE"));
        selectedImageUri = null;
        colorPalette.clear();
        colorPaletteAdapter.notifyDataSetChanged();
        addBannerButton.setText("Save Banner");
    }

    private void startEditing(DocumentSnapshot banner) {
        editingBanner = banner;
        bannerColorEditText.setText(banner.getString("backgroundColor"));
        productIdEditText.setText(banner.getString("productID"));
        categoryNameEditText.setText(banner.getString("categoryName"));

        String imageUrl = banner.getString("imageUrl");
        if (imageUrl != null) {
            selectImagePlaceholder.setVisibility(View.GONE);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            selectedImageView.setImageBitmap(resource);
                            generatePaletteFromBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
        addBannerButton.setText("Update Banner");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> {
        private final List<DocumentSnapshot> banners;

        BannersAdapter(List<DocumentSnapshot> banners) {
            this.banners = banners;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DocumentSnapshot doc = banners.get(position);
            String imageUrl = doc.getString("imageUrl");
            String color = doc.getString("backgroundColor");
            String productId = doc.getString("productID");
            String categoryName = doc.getString("categoryName");

            if (imageUrl != null) {
                Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.bannerImageView);
            }
            holder.bannerColorTextView.setText(color);
            holder.bannerProductIdTextView.setText("ProductID: " + (productId != null ? productId : "N/A"));
            holder.bannerCategoryNameTextView.setText("Category: " + (categoryName != null ? categoryName : "N/A"));

            try {
                DrawableCompat.setTint(
                        DrawableCompat.wrap(holder.bannerColorPreview.getBackground()),
                        Color.parseColor(color)
                );
            } catch (Exception e) {
                // Invalid color
            }

            holder.removeButton.setOnClickListener(v -> removeBannerFromFirestore(doc.getId()));
            holder.editButton.setOnClickListener(v -> startEditing(doc));

        }

        @Override
        public int getItemCount() {
            return banners.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView bannerImageView;
            TextView bannerColorTextView, bannerProductIdTextView, bannerCategoryNameTextView;
            Button removeButton, editButton;
            View bannerColorPreview;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                bannerImageView = itemView.findViewById(R.id.banner_image_view);
                bannerColorTextView = itemView.findViewById(R.id.banner_color_text_view);
                bannerColorPreview = itemView.findViewById(R.id.banner_color_preview);
                bannerProductIdTextView = itemView.findViewById(R.id.banner_product_id_text_view);
                bannerCategoryNameTextView = itemView.findViewById(R.id.banner_category_name_text_view);
                removeButton = itemView.findViewById(R.id.remove_button);
                editButton = itemView.findViewById(R.id.edit_button);
            }
        }
    }
}
